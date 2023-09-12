package mujina.idp;

import mujina.AbstractIntegrationTest;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.config.XmlConfig.xmlConfig;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class MetadataControllerTest extends AbstractIntegrationTest {

  @Value("${idp.base_url}")
  private String idpBaseUrl;

  @Test
  public void metadata() {

    given()
      .header("content-type", "application/json")
      .body("sls")
      .put("/api/slsendpoint")
      .then()
      .statusCode(SC_OK);

    checkMetadata();

    given()
      .header("content-type", "application/json")
      .body("x")
      .put("/api/entityid")
      .then()
      .statusCode(SC_OK);

    checkMetadata();

  }

  private void checkMetadata() {
    given()
      .config(newConfig()
        .xmlConfig(xmlConfig().declareNamespace("md", "urn:oasis:names:tc:SAML:2.0:metadata")))
      .get("/metadata")
      .then()
      .contentType("application/xml")
      .statusCode(SC_OK)
      .body(
        "EntityDescriptor.IDPSSODescriptor.SingleSignOnService.@Location",
        equalTo(idpBaseUrl + "/SingleSignOnService"))
      .body(
        "EntityDescriptor.IDPSSODescriptor.SingleLogoutService.@Location",
        equalTo(idpBaseUrl + "/SingleLogoutService"));
  }

  @AfterClass
  public static void reset() {
    given()
      .header("Content-type", "application/json")
      .post("/api/reset")
      .then()
      .statusCode(SC_OK);
  }


}


