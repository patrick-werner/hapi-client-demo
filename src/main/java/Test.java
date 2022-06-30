import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;

public class Test {

  public static void main(String[] args) {
    FhirContext fhirContext = FhirContext.forR4();
    IParser iParser = fhirContext.newJsonParser().setPrettyPrint(true);
    IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

    //generate Patient
    Patient pat = new Patient();
    pat.setActive(true);
    pat.setGender(AdministrativeGender.FEMALE);
    pat.addName().addGiven("Vorname").setFamily("test");

    //POST to FHIR Server
    MethodOutcome execute = client.create().resource(pat).execute();
    //get returned Ressource from Server
    String patientString = iParser.encodeResourceToString(execute.getResource());
    System.out.println(patientString);

    //validate against Server
    MethodOutcome execute1 = client.validate().resource(execute.getResource()).execute();

    //print Validation Result (OperationOutcome)
    IBaseOperationOutcome operationOutcome = execute1.getOperationOutcome();
    System.out.println(iParser.encodeResourceToString(operationOutcome));

    //create a simple Transaction Bundle and print it to sout
    Bundle bundle = new Bundle();
    bundle.setType(BundleType.TRANSACTION);
    BundleEntryComponent bundleEntryComponent = bundle.addEntry();
    bundleEntryComponent.setResource(pat);
    bundleEntryComponent.getRequest().setMethod(HTTPVerb.POST);
    System.out.println(iParser.encodeResourceToString(bundle));
  }

}
