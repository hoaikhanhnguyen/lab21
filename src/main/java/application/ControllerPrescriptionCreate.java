package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Drug;
import application.model.DrugRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.model.Prescription;
import application.model.PrescriptionRepository;
import application.service.SequenceService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

@Controller
public class ControllerPrescriptionCreate {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	SequenceService sequence;
  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private DrugRepository drugRepository;
  @Autowired
  private PrescriptionRepository prescriptionRepository;

	/*
	 * Doctor requests blank form for new prescription.
	 */
	@GetMapping("/prescription/new")
	public String getPrescriptionForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_create";
	}

	// process data entered on prescription_create form
	@PostMapping("/prescription")
	public String createPrescription(PrescriptionView p, Model model) {

		System.out.println("createPrescription " + p);

		/*
		 * valid doctor name and id
		 */
		Doctor dr = doctorRepository.findByIdAndFirstNameAndLastName(p.getDoctorId(), p.getDoctorFirstName(), p.getDoctorLastName());
		if (dr == null) {
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("prescription", p);
			return "prescription_create";
		}

		/*
		 * valid patient name and id
		 */
		Patient pa = patientRepository.findByIdAndFirstNameAndLastName(p.getPatientId(), p.getPatientFirstName(), p.getPatientLastName());
		if (pa == null) {
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("prescription", p);
			return "prescription_create";
		}
		/*
		 * valid drug name
		 */
		Drug d = drugRepository.findByName(p.getDrugName());
		if (d == null) {
			model.addAttribute("message", "Drug not found.");
			model.addAttribute("prescription", p);
			return "prescription_create";
		}

		/*
		 * insert prescription  
		 */
		int id = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");
		Prescription prescription = new Prescription();
		prescription.setRxid(id);
		prescription.setDoctorId(dr.getId());
		prescription.setPatientId(pa.getId());
		prescription.setDrugName(d.getName());
		prescription.setQuantity(p.getQuantity());
		prescription.setRefills(p.getRefills());
		prescription.setDateCreated(LocalDate.now().toString());

		prescriptionRepository.insert(prescription);

		model.addAttribute("message", "Prescription created.");
		p.setRxid(id);
		model.addAttribute("prescription", p);
		return "prescription_show";
	}
}
