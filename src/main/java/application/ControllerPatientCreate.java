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
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientCreate {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	SequenceService sequence;
  @Autowired
  private PatientRepository patientRepository;

	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String getNewPatientForm(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new PatientView());
		return "patient_register";
	}

	/*
	 * Process data from the patient_register form
	 */
	@PostMapping("/patient/new")
	public String createPatient(PatientView p, Model model) {
		// validate doctor last name
		Doctor d = doctorRepository.findByLastName(p.getPrimaryName());
		if (d == null) {
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("patient", p);
			return "patient_register";
		}

		// get the next unique id for patient.
		int id = sequence.getNextSequence("PATIENT_SEQUENCE");
		// create a model.patient instance
		// copy data from DoctorView to model
		Patient patientM = new Patient();
		patientM.setId(id);
		patientM.setFirstName(p.getFirstName());
		patientM.setLastName(p.getLastName());
		patientM.setBirthdate(p.getBirthdate());
		patientM.setStreet(p.getStreet());
		patientM.setState(p.getState());
		patientM.setCity(p.getCity());
		patientM.setZipcode(p.getZipcode());
		patientM.setSsn(p.getSsn());
		patientM.setPrimaryName(p.getPrimaryName());

		p.setId(id);
		patientRepository.insert(patientM);

		// display message and patient information
		model.addAttribute("message", "Registration successful.");
		model.addAttribute("patient", p);
		return "patient_show";
	}

	/*
	 * Request blank form to search for patient by id and name
	 */
	@GetMapping("/patient/edit")
	public String getSearchForm(Model model) {
		model.addAttribute("patient", new PatientView());
		return "patient_get";
	}

	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String showPatient(PatientView p, Model model) {
		// retrieve patient using the id, last_name e
		Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
		if (patient != null) {
			// copy data from model to view
			p.setFirstName(patient.getFirstName());
			p.setLastName(patient.getLastName());
			p.setBirthdate(patient.getBirthdate());
			p.setStreet(patient.getStreet());
			p.setCity(patient.getCity());
			p.setState(patient.getState());
			p.setZipcode(patient.getZipcode());
			p.setSsn(patient.getSsn());
			p.setPrimaryName(patient.getPrimaryName());

			// if found, return "patient_show", else return error message and "patient_get"
			model.addAttribute("patient", p);
			return "patient_show";
		} else {
				model.addAttribute("message", "Patient not found.");
				model.addAttribute("patient", p);
				return "patient_get";
		}
	}
}
