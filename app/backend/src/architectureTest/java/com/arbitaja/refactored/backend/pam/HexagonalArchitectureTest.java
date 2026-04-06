package com.arbitaja.refactored.backend.pam;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;


public class HexagonalArchitectureTest {

    private static final String DOMAIN = "com.arbitaja.refactored.backend.pam.core.domain..";
    private static final String APPLICATION = "com.arbitaja.refactored.backend.pam.core.application..";
    private static final String PORTS_IN = "com.arbitaja.refactored.backend.pam.core.port.in..";
    private static final String PORTS_OUT = "com.arbitaja.refactored.backend.pam.core.port.out..";
    private static final String ADAPTER_IN = "com.arbitaja.refactored.backend.pam.adapter.in..";
    private static final String ADAPTER_OUT = "com.arbitaja.refactored.backend.pam.adapter.out..";

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.arbitaja.refactored.backend.pam..");

    // Domain must not depend on anything else
    @Test
    void domain_should_not_depend_on_other_layers() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATION, PORTS_IN, PORTS_OUT, ADAPTER_IN, ADAPTER_OUT)
                .check(classes);
    }

    // Application may depend on Domain and Ports, but NOT on Adapters
    @Test
    void application_should_not_depend_on_adapters() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat()
                .resideInAnyPackage(ADAPTER_IN, ADAPTER_OUT)
                .check(classes);
    }

    // Ports-in should depend only on domain (typically, not even on application)
    @Test
    void ports_in_should_not_depend_on_application_or_adapters() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(PORTS_IN)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATION, ADAPTER_IN, ADAPTER_OUT)
                .check(classes);
    }

    // Ports-out should depend only on domain (typically, not even on application)
    @Test
    void ports_out_should_not_depend_on_application_or_adapters() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(PORTS_OUT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATION, ADAPTER_IN, ADAPTER_OUT)
                .check(classes);
    }

    // Adapters-in should depend only on ports-in, application and domain, but NOT on other adapters or ports-out
    @Test
    void adapters_in_should_not_depend_on_adapters_out_or_ports_out() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ADAPTER_IN)
                .should().dependOnClassesThat()
                .resideInAnyPackage(ADAPTER_OUT, PORTS_OUT)
                .check(classes);
    }

    // Adapters-out should depend only on ports-out, application and domain, but NOT on other adapters or ports-in
    @Test
    void adapters_out_should_not_depend_on_adapters_in_or_ports_in() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ADAPTER_OUT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(ADAPTER_IN, PORTS_IN)
                .check(classes);
    }

    // Optionally: Application should not depend on itself cyclically (no self-references)
    @Test
    void no_cycles_between_application_and_ports() {
        slices()
                .matching("com.arbitaja.refactored.backend.pam.core.(application|port.in|port.out)..")
                .should().beFreeOfCycles()
                .check(classes);
    }
}
