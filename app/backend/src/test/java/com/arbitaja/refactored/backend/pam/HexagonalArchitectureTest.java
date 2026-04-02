package com.arbitaja.refactored.backend.pam;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class HexagonalArchitectureTest {

    private static final String DOMAIN = "com.arbitaja.refactored.backend.pam.core.domain..";
    private static final String APPLICATION = "com.arbitaja.refactored.backend.pam.core.application..";
    private static final String PORTS_IN = "com.arbitaja.refactored.backend.pam.core.port.in..";
    private static final String PORTS_OUT = "com.arbitaja.refactored.backend.pam.core.port.out..";
    private static final String ADAPTER_IN = "com.arbitaja.refactored.backend.pam.adapter.in..";
    private static final String ADAPTER_OUT = "com.arbitaja.refactored.backend.pam.adapter.out..";

    private final JavaClasses classes = new ClassFileImporter()
        .importPackages("com.arbitaja.refactored.backend.pam..");

    @Test
    void domainShouldNotDependOnOtherLayers() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(APPLICATION, PORTS_IN, PORTS_OUT, ADAPTER_IN, ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void applicationShouldNotDependOnAdapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat()
            .resideInAnyPackage(ADAPTER_IN, ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void portsInShouldNotDependOnApplicationOrAdapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(PORTS_IN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(APPLICATION, ADAPTER_IN, ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void portsOutShouldNotDependOnApplicationOrAdapters() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(PORTS_OUT)
            .should().dependOnClassesThat()
            .resideInAnyPackage(APPLICATION, ADAPTER_IN, ADAPTER_OUT)
            .check(classes);
    }

    @Test
    void adaptersInShouldNotDependOnAdaptersOutOrPortsOut() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(ADAPTER_IN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(ADAPTER_OUT, PORTS_OUT)
            .check(classes);
    }

    @Test
    void adaptersOutShouldNotDependOnAdaptersInOrPortsIn() {
        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(ADAPTER_OUT)
            .should().dependOnClassesThat()
            .resideInAnyPackage(ADAPTER_IN, PORTS_IN)
            .check(classes);
    }

    @Test
    void noCyclesBetweenApplicationAndPorts() {
        slices()
            .matching("com.arbitaja.refactored.backend.pam.core.(application|port.in|port.out)..")
            .should().beFreeOfCycles()
            .check(classes);
    }
}

