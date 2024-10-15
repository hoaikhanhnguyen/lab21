package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.model.Pharmacy;
import application.model.PharmacyRepository;
import application.model.Prescription;
import application.model.Prescription.FillRequest;
import application.model.PrescriptionRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

@Controller
public class ControllerPrescriptionFill {
	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PharmacyRepository pharmacyRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	/*
	 * Patient requests form to fill prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_fill";
	}

	// process data from prescription_fill form
	@PostMapping("/prescription/fill")
	public String processFillForm(PrescriptionView p, Model model) throws SQLException {
		// find the prescription
		Prescription rx = prescriptionRepository.findById(p.getRxid()).orElse(null);
		if (rx == null) {
			model.addAttribute("message", "Precription not found.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// find patient
		Patient pt = patientRepository.findByLastName(p.getPatientLastName());
		if (pt == null) {
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		// verify that patient last name matches on prescription
		if(rx.getPatientId() != pt.getId()) {
			model.addAttribute("message", "Patient does not match prescription.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// validate pharmacy name and address
		Pharmacy ph = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress());
		if(ph == null) {
			model.addAttribute("message", "Pharmacy not found.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		/*
		 * have we exceeded the number of allowed refills
		 * the first fill is not considered a refill.
		 */
		if(rx.getFills().size() >= rx.getRefills()+1) {
				model.addAttribute("message", "No more fills.");
				model.addAttribute("prescription", p);
				return "prescription_fill";
		}

		FillRequest fr = new FillRequest();
		fr.setPharmacyID(ph.getId());
		fr.setDateFilled(LocalDate.now().toString());

		double cost = 0;
		for(int i = 0; i < ph.getDrugCosts().size(); i++) {
			if(ph.getDrugCosts().get(i).getDrugName().equals(rx.getDrugName())) {
				cost = ph.getDrugCosts().get(i).getCost();
				break;
			}
		}
		fr.setCost(String.valueOf(cost));

		// update rx fills
		if(!rx.getFills().isEmpty()) {
			rx.setRefills(rx.getRefills()-1);
		}
		rx.getFills().add(fr);
		prescriptionRepository.save(rx);

		// get doctor info
		Doctor d = doctorRepository.findById(rx.getDoctorId());
		if (d == null) {
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("patient", p);
			return "prescription_fill";
		}

		// show the updated prescription with the most recent fill information
		p.setDoctorId(rx.getDoctorId());
		p.setDoctorFirstName(d.getFirstName());
		p.setDoctorLastName(d.getLastName());
		p.setPatientId(rx.getPatientId());
		p.setPatientFirstName(pt.getFirstName());
		p.setPatientLastName(pt.getLastName());
		p.setDrugName(rx.getDrugName());
		p.setQuantity(rx.getQuantity());
		p.setRefills(rx.getRefills());
		p.setPharmacyID(ph.getId());
		p.setPharmacyName(ph.getName());
		p.setPharmacyPhone(ph.getPhone());
		p.setDateFilled(LocalDate.now().toString());
		p.setCost(String.valueOf(cost));

		model.addAttribute("message", "Prescription filled.");
		model.addAttribute("prescription", p);
		return "prescription_show";
	}
}