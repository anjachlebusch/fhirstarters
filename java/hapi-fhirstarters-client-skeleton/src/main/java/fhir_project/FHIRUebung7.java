package fhir_project;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;

import java.util.*;

public class FHIRUebung7 {
    public static void main(String[] args) {

        // Create a client
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient("https://funke.imi.uni-luebeck.de/public/fhir");

       //Patient
       Patient antonie = createPatient(client);

       //Blutgruppe
       Observation bloodType = createBloodTypeForPatient(client, antonie);



       //Arztpraxis
       Organization doctorsOffice = createDoctorsOffice(client);

       //Arzt
       Practitioner doctor = createDoctor(client);

      //Arzt-Rolle
       PractitionerRole doctorRole = createDoctorRole(client, doctor, doctorsOffice);

       // Impfung1
       CodeableConcept vaccineCode1 = new CodeableConcept()
          .setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "140", "Influenza, seasonal, injectable, preservative free")))
          .setText("Mutagrip");
       DateTimeType occurenceDate1 = new DateTimeType(new GregorianCalendar(1846, Calendar.OCTOBER, 1));
       Immunization vaccine1 = createImmunization(client, antonie, doctor, vaccineCode1, "123456", occurenceDate1);

       // Impfung2
       CodeableConcept vaccineCode2 = new CodeableConcept().setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "07", "mumps virus vaccine")))
          .setText("Priorix");
       DateTimeType occurenceDate2 = new DateTimeType(new GregorianCalendar(1842, Calendar.APRIL, 12));
       Immunization vaccine2 = createImmunization(client, antonie, doctor, vaccineCode2, "98765", occurenceDate2);

       // Impfung3
       CodeableConcept vaccineCode3 = new CodeableConcept().setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "146", "DTaP,IPV,Hib,HepB")))
          .setText("Infanrix");
       DateTimeType occurenceDate3 = new DateTimeType(new GregorianCalendar(1835, Calendar.JUNE, 23));
       Immunization vaccine3 = createImmunization(client, antonie, doctor, vaccineCode3, "98765", occurenceDate3);

       // Impfung4
       CodeableConcept vaccineCode4 = new CodeableConcept().setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "102", "DTP- Haemophilus influenzae type b conjugate and hepatitis b vaccine")))
          .setText("Repevax");
       DateTimeType occurenceDate4 = new DateTimeType(new GregorianCalendar(1828, Calendar.JANUARY, 2));
       Immunization vaccine4 = createImmunization(client, antonie, doctor, vaccineCode4, "98765", occurenceDate4);

       // Impfung5
       CodeableConcept vaccineCode5 = new CodeableConcept().setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "162", "meningococcal B vaccine, fully recombinant")))
          .setText("Bexsero");
       DateTimeType occurenceDate5 = new DateTimeType(new GregorianCalendar(1930, Calendar.SEPTEMBER, 25));
       Immunization vaccine5 = createImmunization(client, antonie, doctor, vaccineCode5, "243546", occurenceDate5);

       // Impfung6
       CodeableConcept vaccineCode6 = new CodeableConcept().setCoding(Collections.singletonList(new Coding("http://hl7.org/fhir/sid/cvx", "140", "Influenza, seasonal, injectable, preservative free")))
          .setText("Mutagrip");
       DateTimeType occurenceDate6 = new DateTimeType(new GregorianCalendar(1852, Calendar.JULY, 12));
       Immunization vaccine6 = createImmunization(client, antonie, doctor, vaccineCode6, "123456", occurenceDate6);

       //Anti-Körper-Test - Röteln
       CodeableConcept roetelnTest = new CodeableConcept(new Coding("http://loinc.org", "22497-2","Rubella virus Ab [Titer] in Serum"));
       Quantity roetelnResult = new Quantity()
          .setUnit("IE/ml")
          .setValue(8);
       List<CodeableConcept> interpretations = Collections.singletonList(
          new CodeableConcept(
             new Coding("http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation", "NEG", "Negative")
          )
       );
       CodeableConcept testMethod = new CodeableConcept(
          new Coding("http://loinc.org", "LP217198-3", "Rapid immunoassay")
       );
       Observation immunizationTestRoeteln = createImmunizationTest(client, antonie, doctor, roetelnTest, null, roetelnResult, testMethod, interpretations);

       //Anti-Körper-Test - COVID
       CodeableConcept covidTest = new CodeableConcept(new Coding("http://loinc.org", "95209-3", "SARS-CoV+SARS-CoV-2 (COVID-19) Ag [Presence] in Respiratory specimen by Rapid immunoassay"));
       CodeableConcept covidResult = new CodeableConcept(
             new Coding("http://snomed.info/sct", "10828004", "Positive (qualifier value)")
          ).setText("Schutz vorhanden");
       Observation immunizationTestCovid = createImmunizationTest(client, antonie, doctor, covidTest, covidResult, null, null, null);
       Composition c = createComposition(client,ctx,antonie,doctor, Arrays.asList(vaccine1, vaccine2, vaccine3, vaccine4,
          vaccine5, vaccine6), Arrays.asList(immunizationTestCovid, immunizationTestRoeteln), bloodType );
    }



   private static Patient createPatient(IGenericClient client) {
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

      //Riskfactors for immunization
      //TODO: brauchen wir Risikofaktoren?
      RiskAssessment risikoFaktoren = new RiskAssessment();
      risikoFaktoren.setMethod(new CodeableConcept().setCoding(Arrays.asList(new Coding("http ://loinc.org", "66678-4", "Diabetes [PhenX]"),
         new Coding("http ://loinc.org", "11382-9", "Medication allergy - Reported"))));

      MethodOutcome patientOutcome = createResource(client, antonie);
      antonie.setId(patientOutcome.getId());

      return antonie;
   }

   private static Observation createBloodTypeForPatient(IGenericClient client, Patient patient) {
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
         .setSubject(new Reference(patient))
         .setEffective(new DateTimeType(new GregorianCalendar(1900, Calendar.APRIL, 12)))
         .setValue(new CodeableConcept().setCoding(
            Arrays.asList(new Coding("http://snomed.info/sct", "112144000", "Blood group A (finding)"),
               new Coding("http://snomed.info/sct", "165747007", "RhD positive (finding)")))
         );
      MethodOutcome bloodTypeOutcome = createResource(client, patient);
      bloodType.setId(bloodTypeOutcome.getId());
      return bloodType;
   }

   private static Organization createDoctorsOffice(IGenericClient client) {
      Organization doctorsOffice = new Organization()
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

      MethodOutcome arztpraxisOutcome = createResource(client, doctorsOffice);
      doctorsOffice.setId(arztpraxisOutcome.getId());
      return doctorsOffice;
   }

   private static Practitioner createDoctor(IGenericClient client) {
      Practitioner doctor = new Practitioner()
         .addName(new HumanName()
            .addPrefix("Dr.")
            .addGiven("Frauke")
            .setFamily("Lehmann")
         );
      MethodOutcome doctorOutcome = createResource(client, doctor);
      doctor.setId(doctorOutcome.getId());
      return doctor;
   }

   private static PractitionerRole createDoctorRole(IGenericClient client, Practitioner doctor, Organization doctorsOffice) {
      PractitionerRole doctorRole = new PractitionerRole()
         .setPractitionerTarget(doctor).setPractitioner(new Reference(doctor))
         .setOrganizationTarget(doctorsOffice).setOrganization(new Reference(doctorsOffice))
         .addCode(new CodeableConcept(new Coding("http://hl7.org/fhir/ValueSet/practitioner-role", "doctor",
         "A qualified/registered medical practitioner")));
      MethodOutcome doctorRoleOutcome = createResource(client, doctorRole);
      doctorRole.setId(doctorRoleOutcome.getId());
      return doctorRole;
   }

   private static Immunization createImmunization(IGenericClient client, Patient patient, Practitioner doctor,
                                                  CodeableConcept vaccineCode, String lotNumber, DateTimeType occurence) {
      Immunization vaccine = new Immunization()
         .setPatient(new Reference(patient))
         .setVaccineCode(vaccineCode)
         .setLotNumber(lotNumber)
         .setOccurrence(occurence)
         .setPerformer(Collections.singletonList(new Immunization.ImmunizationPerformerComponent(new Reference(doctor))));

      MethodOutcome vaccineOutcome = createResource(client, vaccine);
      vaccine.setId(vaccineOutcome.getId());
      return vaccine;
   }

   private static Observation createImmunizationTest(IGenericClient client, Patient patient, Practitioner performer,
                                                     CodeableConcept testType, CodeableConcept testResult, Quantity quantitativeResult,
                                                     CodeableConcept method, List<CodeableConcept> interpretations) {
      Observation immunizationTest = new Observation()
         .addCategory(
            new CodeableConcept(
               new Coding("http://terminology.hl7.org/CodeSystem/observation-category", ObservationCategory.LABORATORY.toCode(), ObservationCategory.LABORATORY.getDisplay())
            ))
         .setCode(testType)
         .setSubject(new Reference(patient))
         .addPerformer(new Reference(performer));
         if(testResult != null) {
            immunizationTest.setValue(testResult);
         } else if(quantitativeResult != null) {
            immunizationTest.setValue(quantitativeResult);
         }
         if(interpretations != null){
            immunizationTest.setInterpretation(interpretations);
         }
      if(method != null) {
         immunizationTest.setMethod(method);
      }

      MethodOutcome immunizationTestRoetelnOutcome = createResource(client, immunizationTest);
      immunizationTest.setId(immunizationTestRoetelnOutcome.getId());
      return immunizationTest;
   }

   private static MethodOutcome createResource(IGenericClient client, Resource res)
   {
      MethodOutcome outcome = client.create()
         .resource(res)
         .prettyPrint()
         .encodedJson()
         .execute();
      System.out.println("ID of created Resource: " + outcome.getId());
      return outcome;
   }

   private static Composition createComposition(IGenericClient client,FhirContext ctx,Patient patient, Practitioner performer,
                                                List<Immunization> I, List<Observation> antibodyTests, Observation bloodType) {
      Composition comp = new Composition()
         .setStatus(Composition.CompositionStatus.FINAL)
         .setType(new CodeableConcept(
            new Coding("http://loinc.org", "11503-0", "Medical records")
         ))
         .setDate(new GregorianCalendar(1846, Calendar.OCTOBER, 1).getTime())
         .setAuthor(Collections.singletonList(new Reference(performer)))
         .setTitle("INTERNATIONALE BESCHEINIGUNGEN ÜBER IMPFUNGEN UND IMPFBUCH");;
      // todo: text
      Composition.SectionComponent deckblattSection =
         new Composition.SectionComponent()
         .setTitle("Patient");
      deckblattSection.addEntry(new Reference(patient));
      comp.addSection(deckblattSection);

      Composition.SectionComponent vaccineSection =
         new Composition.SectionComponent()
            .setTitle("Impfungen");
      for(Immunization immunization : I) {
         vaccineSection.addEntry(new Reference(immunization));
      }
      comp.addSection(vaccineSection);


      Composition.SectionComponent antibodyTestSection =
         new Composition.SectionComponent()
            .setTitle("Anti-Körper-Tests");
      for(Observation antibodytest : antibodyTests) {
         antibodyTestSection.addEntry(new Reference(antibodytest));
      }
      comp.addSection(antibodyTestSection);

      Composition.SectionComponent riskfactorSection =
         new Composition.SectionComponent()
            .setTitle("Risikofaktoren bei Impfungen");
      //TODO: add Risikofaktoren
      comp.addSection(riskfactorSection);

      Composition.SectionComponent bloodgroupSection =
         new Composition.SectionComponent()
            .setTitle("Blutgruppe und Rh-Faktor");
      bloodgroupSection.addEntry(new Reference(bloodType));
      comp.addSection(bloodgroupSection);

      // todo: check subject
//      List<Immunization> Immunizations = new ArrayList<>();
//      Bundle bundle = client.search().forResource(Immunization.class).returnBundle(Bundle.class).execute();
//      Immunizations.addAll(BundleUtil.toListOfResourcesOfType(ctx, bundle, Immunization.class));
//      // Load the subsequent pages
//      while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
//         bundle = client.loadPage().next(bundle).execute();
//         Immunizations.addAll(BundleUtil.toListOfResourcesOfType(ctx, bundle, Immunization.class));
//      }
//      deckblattSection.addEntry().setResource(performer);
//      comp.addSection(deckblattSection);
//      //comp.addSection().addEntry().setResource(new AllergyIntolerance().addNote(new Annotation().setText("Section0_Allergy0")));
//      comp.addSection().addEntry().setResource(new AllergyIntolerance().addNote(new Annotation().setText("Section1_Allergy0")));
//      comp.addSection().addEntry().setResource(patient);
//      comp.addSection().addEntry().setResource(I);
//      IParser parser = ctx.newJsonParser().setPrettyPrint(true);
//      //IParser parser = ctx.newXmlParser().setPrettyPrint(true);
//      String string = parser.encodeResourceToString(comp);
//
//
//      Composition parsed = parser.parseResource(Composition.class, string);
//      parsed.getSection().remove(0);
//
//      string = parser.encodeResourceToString(parsed);
//
//      parsed = parser.parseResource(Composition.class, string);
      MethodOutcome compOutcome = createResource(client, comp);
      comp.setId(compOutcome.getId());

      return comp;

   }



}
