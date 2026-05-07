package com.arbitaja.refactored.backend.scoring;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;


public class HexagonalArchitectureTest {

    private static final String SCORING_DOMAIN = "com.arbitaja.refactored.backend.scoring.core.domain..";
    private static final String SCORING_APPLICATION = "com.arbitaja.refactored.backend.scoring.core.application..";
    private static final String SCORING_PORTS_IN = "com.arbitaja.refactored.backend.scoring.core.port.in..";
    private static final String SCORING_PORTS_OUT = "com.arbitaja.refactored.backend.scoring.core.port.out..";
    private static final String SCORING_ADAPTER_IN = "com.arbitaja.refactored.backend.scoring.adapter.in..";
    private static final String SCORING_ADAPTER_OUT = "com.arbitaja.refactored.backend.scoring.adapter.out..";

    private final JavaClasses classes = new ClassFileImporter()
        .withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages("com.arbitaja.refactored.backend..");


    @Test
    void scoring_domain_should_not_depend_on_other_layers() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_DOMAIN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_APPLICATION, SCORING_PORTS_IN, SCORING_PORTS_OUT,
                SCORING_ADAPTER_IN, SCORING_ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void scoring_application_should_not_depend_on_adapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_APPLICATION)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_ADAPTER_IN, SCORING_ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void scoring_ports_in_should_not_depend_on_application_or_adapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_PORTS_IN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_APPLICATION, SCORING_ADAPTER_IN, SCORING_ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void scoring_ports_out_should_not_depend_on_application_or_adapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_PORTS_OUT)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_APPLICATION, SCORING_ADAPTER_IN, SCORING_ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void scoring_adapters_in_should_not_depend_on_adapters_out_or_ports_out() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_ADAPTER_IN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_ADAPTER_OUT, SCORING_PORTS_OUT)
            .check(classes);
    }

    @Test
    void scoring_adapters_out_should_not_depend_on_adapters_in_or_ports_in() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(SCORING_ADAPTER_OUT)
            .should().dependOnClassesThat()
            .resideInAnyPackage(SCORING_ADAPTER_IN, SCORING_PORTS_IN)
            .check(classes);
    }

    @Test
    void scoring_no_cycles_between_application_and_ports() {
        slices()
            .matching("com.arbitaja.refactored.backend.scoring.core.(application|port.in|port.out)..")
            .should().beFreeOfCycles()
            .check(classes);
    }

    @Test
    void scoring_web_controllers_should_not_depend_on_pam_or_competition_web_annotations() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage("com.arbitaja.refactored.backend.scoring.adapter.in.web..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "com.arbitaja.refactored.backend.pam.adapter.in.web.annotations..",
                "com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.."
            )
            .check(classes);
    }

    @Test
    void scoring_module_should_not_depend_on_unrefactored_backend_package() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage("com.arbitaja.refactored.backend.scoring..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.arbitaja.backend..").allowEmptyShould(true)
            .check(classes);
    }

    @Test
    void scoring_module_should_not_depend_on_competition_module() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage("com.arbitaja.refactored.backend.scoring..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.arbitaja.refactored.backend.competition..").allowEmptyShould(true)
            .check(classes);
    }
}
