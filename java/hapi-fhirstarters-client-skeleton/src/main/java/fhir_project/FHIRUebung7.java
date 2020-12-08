package fhir_project;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FHIRUebung7 {
    public static void main(String[] args) {
        // Create a client
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient("https://funke.imi.uni-luebeck.de/public/fhir");

       // Create a bundle that will be used as a transaction
       Bundle bundle = new Bundle();
       bundle.setType(Bundle.BundleType.TRANSACTION);

       // Create a patient
       // Version from Hannes
       // Empty Patient Instance
       Patient antonie = new Patient();

       // Identifier
       Identifier identifier = new Identifier();
       identifier.setValue("1234567890");
       identifier.setSystem("http://kh-uzl.de/fhir/patients");
       antonie.addIdentifier(identifier);

       // Official Name
       HumanName gruenlich = new HumanName();
       gruenlich.addGiven("Antonie");
       gruenlich.setFamily("Grünlich");
       gruenlich.setUse(HumanName.NameUse.OFFICIAL);
       antonie.addName(gruenlich);

       // Birthday
       Calendar cal = Calendar.getInstance();
       // CAVE: Java integer starts by 0!
       cal.set(1827, 7, 6);
       antonie.setBirthDate(cal.getTime());

       //Birthplace
       antonie.addExtension().setUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace")
          .setValue(new StringType("Hamburg"));

       //Address
       antonie.addAddress(
          new Address()
             .setLine(Collections.singletonList(new StringType("Stresemannstraße 12")))
             .setPostalCode("22179")
             .setCity("Hamburg")
       );

       //Passport number
       antonie.addExtension().setUrl("http://acme.org/fhir/StructureDefinition/passport-number")
        .setValue(new StringType("12345ABC"));

       //add patient to bundle
       bundle.addEntry()
          .setResource(antonie)
          .getRequest()
          .setUrl("Patient")
          .setMethod(Bundle.HTTPVerb.POST);

       //Riskfactors for immunization
       //TODO: brauchen wir Risikofaktoren?
       RiskAssessment risikoFaktoren = new RiskAssessment();

       //Blood type
       Observation bloodType = new Observation()
          .setStatus(Observation.ObservationStatus.FINAL)
          .setCategory(
             Collections.singletonList(new CodeableConcept().setCoding(
                Collections.singletonList(new Coding("http://terminology.hl7.org/CodeSystem/observation-category", "Laboratory", "The results of observations generated by laboratories. Laboratory results are typically generated by laboratories providing analytic services in areas such as chemistry, hematology, serology, histology, cytology, anatomic pathology (including digital pathology), microbiology, and/or virology. These observations are based on analysis of specimens obtained from the patient and submitted to the laboratory."))
             ))
          )
          .setCode(
             new CodeableConcept().setCoding(
                Collections.singletonList(new Coding("http ://loinc.org", "882-1", "ABO and Rh group [Type] in Blood"))
             )
          )
          .setSubject(new Reference(antonie))
          .setEffective(new DateTimeType(new GregorianCalendar(1900, Calendar.APRIL, 12)))
          .setValue(new CodeableConcept().setCoding(
             Arrays.asList(new Coding("http://snomed.info/sct", "112144000", "Blood group A (finding)"),
             new Coding("http://snomed.info/sct", "165747007", "RhD positive (finding)")))
          );

       //add to bundle
       bundle.addEntry()
          .setResource(bloodType)
          .getRequest()
          .setUrl("Observation")
          .setMethod(Bundle.HTTPVerb.POST);

       //Arztpraxis
       Organization Arztpraxis = new Organization()
          .addAlias("Schöne Praxis")
          .addAddress(
             new Address()
                .setLine(Collections.singletonList(new StringType("Max-Brauer-Allee 122")))
                .setPostalCode("22179")
                .setCity("Hamburg")
          )
          .addTelecom(new ContactPoint()
             .setSystem(ContactPoint.ContactPointSystem.PHONE)
             .setValue("040/678123"));

       //organization
       bundle.addEntry()
          .setResource(Arztpraxis)
          .getRequest()
          .setUrl("Organization")
          .setMethod(Bundle.HTTPVerb.POST);

       //Arzt
       Practitioner doctor= new Practitioner();
       HumanName doctorsName = new HumanName();
       doctorsName.addPrefix("Dr.");
       doctorsName.addGiven("Frauke");
       doctorsName.setFamily("Lehmann");
       doctor.addName(doctorsName);
       //TODO: Unterschrift als Identifier?
       doctor.addIdentifier();
       //TODO: brauchen wir eine Organization (Arztpraxis) oder soll die Adresse + Telefon direkt an den Arzt?

       PractitionerRole doctorRole = new PractitionerRole();
       doctorRole.setPractitionerTarget(doctor).setPractitioner(new Reference(doctor));
       doctorRole.setOrganizationTarget(Arztpraxis).setOrganization(new Reference(Arztpraxis));
       doctorRole.addCode(new CodeableConcept(new Coding("http://hl7.org/fhir/ValueSet/practitioner-role", "doctor",
          "A qualified/registered medical practitioner")));

       //add to bundle
       bundle.addEntry()
          .setResource(doctor)
          .getRequest()
          .setUrl("Practitioner")
          .setMethod(Bundle.HTTPVerb.POST);

       //add to bundle
       bundle.addEntry()
          .setResource(doctorRole)
          .getRequest()
          .setUrl("PractitionerRole")
          .setMethod(Bundle.HTTPVerb.POST);

      //Appointents
       Appointment vaccineAppointment = new Appointment()
          .setStart(new GregorianCalendar(1846, Calendar.OCTOBER, 1).getTime());
       bundle.addEntry()
          .setResource(vaccineAppointment)
          .getRequest()
          .setUrl("Appointment")
          .setMethod(Bundle.HTTPVerb.POST);

       Appointment covidAntiGenAppointment = new Appointment()
          .setStart(new GregorianCalendar(1846, Calendar.OCTOBER, 1).getTime());
       bundle.addEntry()
          .setResource(covidAntiGenAppointment)
          .getRequest()
          .setUrl("Appointment")
          .setMethod(Bundle.HTTPVerb.POST);

       Appointment roetelnAntiGenAppointment = new Appointment()
          .setStart(new GregorianCalendar(1846, Calendar.OCTOBER, 1).getTime());
       bundle.addEntry()
          .setResource(roetelnAntiGenAppointment)
          .getRequest()
          .setUrl("Appointment")
          .setMethod(Bundle.HTTPVerb.POST);

       //Encounter - Impfung
       Encounter vaccineEncounter = new Encounter()
         .setStatus(Encounter.EncounterStatus.FINISHED)
         .setClass_(new Coding("http://terminology.hl7.org/ValueSet/v3-ActEncounterCode", "AMB",
          "A comprehensive term for health care provided in a healthcare facility (e.g. a practitioneraTMs office, clinic setting, or hospital) on a nonresident basis. The term ambulatory usually implies that the patient has come to the location and is not assigned to a bed. Sometimes referred to as an outpatient encounter."))
         .setServiceType(
          new CodeableConcept(new Coding("http://hl7.org/fhir/ValueSet/service-type", "57", "Immunization")))
         .setSubjectTarget(antonie).setSubject(new Reference(antonie))
         .addParticipant(new Encounter.EncounterParticipantComponent().setIndividual(new Reference(doctor)))
         .addReasonCode(
             new CodeableConcept(
                new Coding("http://snomed.info/sct", "185346005", "Encounter for sign (procedure)")
             ).setText("Immunization")
         )
         .addAppointment(new Reference(vaccineAppointment));

       //add to bundle
       bundle.addEntry()
          .setResource(vaccineEncounter)
          .getRequest()
          .setUrl("Encounter")
          .setMethod(Bundle.HTTPVerb.POST);


       //Encounter - Anti-Körper Röteln
       Encounter roetelnEncounter = new Encounter()
       .setStatus(Encounter.EncounterStatus.FINISHED)
       .setClass_(new Coding("http://terminology.hl7.org/ValueSet/v3-ActEncounterCode", "AMB",
          "A comprehensive term for health care provided in a healthcare facility (e.g. a practitioneraTMs office, clinic setting, or hospital) on a nonresident basis. The term ambulatory usually implies that the patient has come to the location and is not assigned to a bed. Sometimes referred to as an outpatient encounter."))
       .setServiceType(
          new CodeableConcept(new Coding("http://hl7.org/fhir/ValueSet/service-type", "57", "Immunization")))
       .setSubjectTarget(antonie).setSubject(new Reference(antonie))
       .addParticipant(new Encounter.EncounterParticipantComponent().setIndividual(new Reference(doctor)))
       .addReasonCode(
          new CodeableConcept(
             new Coding("http://snomed.info/sct", "185346005", "Encounter for sign (procedure)")
          ).setText("anti-gen test")
       )
       .addAppointment(new Reference(roetelnAntiGenAppointment));

       //add to bundle
       bundle.addEntry()
          .setResource(roetelnEncounter)
          .getRequest()
          .setUrl("Encounter")
          .setMethod(Bundle.HTTPVerb.POST);

       //Encounter - Anti-Körper COVID
       Encounter covidEncounter = new Encounter()
       .setStatus(Encounter.EncounterStatus.FINISHED)
       .setClass_(new Coding("http://terminology.hl7.org/ValueSet/v3-ActEncounterCode", "AMB",
          "A comprehensive term for health care provided in a healthcare facility (e.g. a practitioneraTMs office, clinic setting, or hospital) on a nonresident basis. The term ambulatory usually implies that the patient has come to the location and is not assigned to a bed. Sometimes referred to as an outpatient encounter."))
       .setServiceType(
          new CodeableConcept(new Coding("http://hl7.org/fhir/ValueSet/service-type", "57", "Immunization")))
       .setSubjectTarget(antonie).setSubject(new Reference(antonie))
       .addParticipant(new Encounter.EncounterParticipantComponent().setIndividual(new Reference(doctor)))
       .addReasonCode(
          new CodeableConcept(
             new Coding("http://snomed.info/sct", "185346005", "Encounter for sign (procedure)")
          ).setText("anti-gen test")
       )
       .addAppointment(new Reference(covidAntiGenAppointment));

       //add to bundle
       bundle.addEntry()
          .setResource(covidEncounter)
          .getRequest()
          .setUrl("Encounter")
          .setMethod(Bundle.HTTPVerb.POST);

       // Impfung
       Immunization Impfung = new Immunization()
          .setPatient(new Reference(antonie))
          .setVaccineCode(new CodeableConcept()
             .setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "140", "Influenza, seasonal, injectable, preservative free")))
          )
          .setLotNumber("123987")
          .setOccurrence(new DateTimeType(new GregorianCalendar(1895, Calendar.OCTOBER, 9)))
          .setPerformer(Collections.singletonList(new Immunization.ImmunizationPerformerComponent(new Reference(doctor))))
          .setEncounter(new Reference(vaccineEncounter));
          //.setManufacturer(/*TODO: manufacturer oder Name der Impfung?*/)

       //add to bundle
       bundle.addEntry()
          .setResource(Impfung)
          .getRequest()
          .setUrl("Immunization")
          .setMethod(Bundle.HTTPVerb.POST);

       //Anti-Körper-Test - Röteln
       Observation immunizationTestRoeteln = new Observation()
          .addCategory(
             new CodeableConcept(
                new Coding("http://terminology.hl7.org/CodeSystem/observation-category", ObservationCategory.LABORATORY.toCode(), ObservationCategory.LABORATORY.getDisplay())
             ))
          .setCode(
             new CodeableConcept(
                new Coding("http://loinc.org", "74415-1","display: Rubella virus IgG Ab [Presence] in Body fluid by Immunoassay")
             )
          )
          .setSubject(new Reference(antonie))
          .addPerformer(new Reference(doctor))
          .setValue(new CodeableConcept(
             new Coding("http://snomed.info/sct", "260385009", "Negative (qualifier value)")
          ).setText("Schutz nicht vorhanden"))
          .setEncounter(new Reference(roetelnEncounter));

       //add to bundle
       bundle.addEntry()
          .setResource(immunizationTestRoeteln)
          .getRequest()
          .setUrl("Observation")
          .setMethod(Bundle.HTTPVerb.POST);


       //Anti-Körper-Test - COVID
       Observation immunizationTestCovid = new Observation()
          .addCategory(
             new CodeableConcept(
                new Coding("http://terminology.hl7.org/CodeSystem/observation-category", ObservationCategory.LABORATORY.toCode(), ObservationCategory.LABORATORY.getDisplay())
             ))
          .setCode(
             new CodeableConcept(
                new Coding("http://loinc.org", "95209-3", "SARS-CoV+SARS-CoV-2 (COVID-19) Ag [Presence] in Respiratory specimen by Rapid immunoassay")
             )
          )
          .setSubject(new Reference(antonie))
          .addPerformer(new Reference(doctor))
          .setValue(new CodeableConcept(
             new Coding("http://snomed.info/sct", "10828004", "Positive (qualifier value)")
          ).setText("Schutz vorhanden"))
          .setEncounter(new Reference(covidEncounter));

       //add to bundle
       bundle.addEntry()
          .setResource(immunizationTestCovid)
          .getRequest()
          .setUrl("Observation")
          .setMethod(Bundle.HTTPVerb.POST);

       //Upload bundle to server
       Bundle bundleOutcome = client.transaction().withBundle(bundle).prettyPrint().encodedJson().execute();
       System.out.println(bundleOutcome);
    }
    //TODO: wie viele Antikörper-Tests / Impfungen



}
