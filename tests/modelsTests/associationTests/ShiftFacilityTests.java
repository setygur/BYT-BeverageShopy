package modelsTests.associationTests;

import models.*;
import models.utils.Address;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftFacilityTests {

    private LocalDateTime now() { return LocalDateTime.now(); }

    // ----------------------------------------------------
    // 1. CREATION TEST (BIDIRECTIONAL)
    // ----------------------------------------------------
    @Test
    public void testCreateAssociationShiftFacility() {
        Facility f = new Facility(new Address("A", "Street", "1", 12345));
        Shift s = new Shift(now(), now().plusHours(2));

        s.addFacility(f);

        assertTrue(s.getFacilities().contains(f));   // direct
        assertTrue(f.getShifts().contains(s));       // reverse
    }

    // ----------------------------------------------------
    // 2. DELETION TEST (BIDIRECTIONAL)
    // ----------------------------------------------------
    @Test
    public void testDeleteAssociationShiftFacility() {
        Facility f = new Facility(new Address("A", "Street", "1", 12345));
        Shift s = new Shift(now(), now().plusHours(2));

        s.addFacility(f);
        s.removeFacility(f);

        assertFalse(s.getFacilities().contains(f));
        assertFalse(f.getShifts().contains(s));
    }

    // ----------------------------------------------------
    // 3. MODIFICATION TEST
    // (modify — by reassigning to a different facility)
    // ----------------------------------------------------
    @Test
    public void testModifyFacilityAssociation() {
        Facility f1 = new Facility(new Address("A", "Street", "1", 12345));
        Facility f2 = new Facility(new Address("B", "Street", "3", 12345));
        Shift s = new Shift(now(), now().plusHours(2));

        s.addFacility(f1);
        s.addFacility(f2);

        assertEquals(2, s.getFacilities().size());
        assertTrue(f1.getShifts().contains(s));
        assertTrue(f2.getShifts().contains(s));
    }

    // ----------------------------------------------------
    // 4. EXCEPTION TEST — adding null shift to facility
    // ----------------------------------------------------
    @Test
    public void testAddNullShiftThrows() {
        Facility f = new Facility(new Address("A", "Street", "1", 12345));
        assertThrows(IllegalArgumentException.class, () -> f.addShift(null));
    }

    // ----------------------------------------------------
    // 5. EXCEPTION TEST — Shift must have 1..* facilities
    // ----------------------------------------------------
    @Test
    public void testShiftValidateFailsWithoutFacility() {
        Shift s = new Shift(now(), now().plusHours(2));
        assertThrows(ValidationException.class, s::validate);
    }

    // ----------------------------------------------------
    // 6. EXCEPTION TEST — Facility must have 1..* shifts
    // ----------------------------------------------------
    @Test
    public void testFacilityValidateFailsWithoutShift() {
        Facility f = new Facility(new Address("A", "Street", "1", 12345));
        assertThrows(ValidationException.class, f::validate);
    }
}