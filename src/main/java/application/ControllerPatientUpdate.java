package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientUpdate {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	SequenceService sequence;
	@Autowired
	private PatientRepository patientRepository;
	
	/*
	 *  Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{id}")
	public String getUpdateForm(@PathVariable int id, Model model) {

		PatientView pv = new PatientView();
		// search for patient by id
		Patient p = patientRepository.findById(id);
		if (p == null) {
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("patient", pv);
			return "patient_edit";
		}
		pv.setId(id);
		pv.setFirstName(p.getFirstName());
		pv.setLastName(p.getLastName());
		pv.setBirthdate(p.getBirthdate());
		pv.setStreet(p.getStreet());
		pv.setCity(p.getCity());
		pv.setState(p.getState());
		pv.setZipcode(p.getZipcode());
		pv.setSsn(p.getSsn());
		pv.setPrimaryName(p.getPrimaryName());

		model.addAttribute("patient", pv);
		return "patient_edit";
	}

	/*
	 * Process changes from patient_edit form
	 *  Primary doctor, street, city, state, zip can be changed
	 *  ssn, patient id, name, birthdate, ssn are read only in template.
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(PatientView p, Model model) {
		//validate doctor last name
		Doctor d = doctorRepository.findByLastName(p.getPrimaryName());
		if (d == null) {
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("patient", p);
			return "patient_edit";
		}

		// update patient profile data in database
		Patient pm = patientRepository.findById(p.getId());
		pm.setStreet(p.getStreet());
		pm.setState(p.getState());
		pm.setCity(p.getCity());
		pm.setZipcode(p.getZipcode());
		pm.setPrimaryName(p.getPrimaryName());
		patientRepository.save(pm);

		// display patient data and the generated patient ID,  and success message
		model.addAttribute("message", "Update successful.");
		model.addAttribute("patient", p);
		return "patient_show";
	}
}
