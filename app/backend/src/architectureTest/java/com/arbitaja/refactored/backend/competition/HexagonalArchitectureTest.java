package com.arbitaja.refactored.backend.competition;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;


public class HexagonalArchitectureTest {

  private static final String COMPETITION_DOMAIN = "com.arbitaja.refactored.backend.competition.core.domain..";
  private static final String COMPETITION_APPLICATION = "com.arbitaja.refactored.backend.competition.core.application..";
  private static final String COMPETITION_PORTS_IN = "com.arbitaja.refactored.backend.competition.core.port.in..";
  private static final String COMPETITION_PORTS_OUT = "com.arbitaja.refactored.backend.competition.core.port.out..";
  private static final String COMPETITION_ADAPTER_IN = "com.arbitaja.refactored.backend.competition.adapter.in..";
  private static final String COMPETITION_ADAPTER_OUT = "com.arbitaja.refactored.backend.competition.adapter.out..";

  private final JavaClasses classes = new ClassFileImporter()
      .withImportOption(new ImportOption.DoNotIncludeTests())
      .importPackages("com.arbitaja.refactored.backend..");


  @Test
  void competition_domain_should_not_depend_on_other_layers() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_DOMAIN)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_APPLICATION, COMPETITION_PORTS_IN, COMPETITION_PORTS_OUT,
            COMPETITION_ADAPTER_IN, COMPETITION_ADAPTER_OUT)
        .check(classes);
  }

  @Test
  void competition_application_should_not_depend_on_adapters() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_APPLICATION)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_ADAPTER_IN, COMPETITION_ADAPTER_OUT)
        .check(classes);
  }

  @Test
  void competition_ports_in_should_not_depend_on_application_or_adapters() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_PORTS_IN)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_APPLICATION, COMPETITION_ADAPTER_IN, COMPETITION_ADAPTER_OUT)
        .check(classes);
  }

  @Test
  void competition_ports_out_should_not_depend_on_application_or_adapters() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_PORTS_OUT)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_APPLICATION, COMPETITION_ADAPTER_IN, COMPETITION_ADAPTER_OUT)
        .check(classes);
  }

  @Test
  void competition_adapters_in_should_not_depend_on_adapters_out_or_ports_out() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_ADAPTER_IN)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_ADAPTER_OUT, COMPETITION_PORTS_OUT)
        .check(classes);
  }

  @Test
  void competition_adapters_out_should_not_depend_on_adapters_in_or_ports_in() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage(COMPETITION_ADAPTER_OUT)
        .should().dependOnClassesThat()
        .resideInAnyPackage(COMPETITION_ADAPTER_IN, COMPETITION_PORTS_IN)
        .check(classes);
  }

  @Test
  void competition_no_cycles_between_application_and_ports() {
    slices()
        .matching("com.arbitaja.refactored.backend.competition.core.(application|port.in|port.out)..")
        .should().beFreeOfCycles()
        .check(classes);
  }

  @Test
  void competition_web_controllers_should_not_depend_on_pam_web_annotations() {
    ArchRuleDefinition.noClasses()
        .that().resideInAnyPackage(
            "com.arbitaja.refactored.backend.competition.adapter.in.web.competition..",
            "com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.."
        )
        .should().dependOnClassesThat()
        .resideInAPackage("com.arbitaja.refactored.backend.pam.adapter.in.web.annotations..")
        .check(classes);
  }

  @Test
  void competition_module_should_not_depend_on_unrefactored_backend_package() {
    ArchRuleDefinition.noClasses()
        .that().resideInAPackage("com.arbitaja.refactored.backend.competition..")
        .should().dependOnClassesThat()
        .resideInAPackage("com.arbitaja.backend..").allowEmptyShould(true)
        .check(classes);
  }
}

