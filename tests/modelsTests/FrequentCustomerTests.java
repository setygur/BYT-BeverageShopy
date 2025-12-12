package modelsTests;

import models.FrequentCustomer;
import modelsTests.utilTests.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//TODO Need to fix some tests
public class FrequentCustomerTests {

    @BeforeEach
    void reset() {
        TestUtils.resetObjectLists(FrequentCustomer.class);
    }

    private FrequentCustomer fc(String name, String surname, String email, String phone, int orders) {
        return new FrequentCustomer(name, surname, email, phone, orders);
    }

    private FrequentCustomer fc(String name, int orders) {
        return fc(name, "Surname", name.toLowerCase() + "@example.com", "123456789", orders);
    }

    @Test
    void constructor_addsToStaticLists_whenValid() {
        int beforeFC = FrequentCustomer.frequentCustomers.size();

        FrequentCustomer a = fc("Alice", 5);

        assertNotNull(a);
        assertEquals(beforeFC + 1, FrequentCustomer.frequentCustomers.size());
        assertTrue(FrequentCustomer.frequentCustomers.contains(a));
    }

    @Test
    void constructor_throws_whenNameBlank_dueToPersonNotBlank() {
        assertThrows(ValidationException.class, () ->
                fc("   ", "Surname", "a@example.com", "123", 1)
        );
    }

    @Test
    void constructor_throws_whenSurnameBlank_dueToPersonNotBlank() {
        assertThrows(ValidationException.class, () ->
                fc("Alice", "   ", "a@example.com", "123", 1)
        );
    }

    @Test
    void constructor_throws_whenEmailBlank_dueToPersonNotBlank() {
        assertThrows(ValidationException.class, () ->
                fc("Alice", "Surname", "   ", "123", 1)
        );
    }

    @Test
    void constructor_throws_whenEmailNotUnique_dueToPersonUnique() {
        assertDoesNotThrow(() -> fc("Alice", "Surname", "dup@example.com", "123", 1));

        assertThrows(ValidationException.class, () ->
                fc("Bob", "Surname", "dup@example.com", "456", 2)
        );
    }

    @Test
    void constructor_throws_whenPhoneBlank_dueToFrequentCustomerNotBlank() {
        assertThrows(ValidationException.class, () ->
                fc("Alice", "Surname", "a@example.com", "   ", 1)
        );
    }

    @Test
    void getAmountOfOrders_returnsProvidedValue() {
        FrequentCustomer a = fc("Alice", 7);
        assertEquals(7, a.getAmountOfOrders());
    }

    @Test
    void getCalculatedDiscount_noReferrals_equalsAmountOfOrders() {
        FrequentCustomer a = fc("Alice", 10);
        assertEquals(10.0, a.getCalculatedDiscount(), 0.0001);
    }

    @Test
    void getCalculatedDiscount_withReferrals_addsHalfOfEachReferredOrders() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer r1 = fc("Bob", 4);
        FrequentCustomer r2 = fc("Carol", 3);

        @SuppressWarnings("unchecked")
        List<FrequentCustomer> referred =
                TestUtils.getField(a, "referredCustomers", List.class);

        referred.add(r1);
        referred.add(r2);

        assertEquals(13.5, a.getCalculatedDiscount(), 0.0001);
    }

    @Test
    void addReferredCustomer_throws_whenNull() {
        FrequentCustomer a = fc("Alice", 1);
        assertThrows(ValidationException.class, () -> a.addReferredCustomer(null));
    }

    @Test
    void addReferrer_throws_whenNull() {
        FrequentCustomer a = fc("Alice", 1);
        assertThrows(ValidationException.class, () -> a.addReferrer(null));
    }

    @Test
    void addReferredCustomer_throws_whenCustomerAlreadyReferredBySomeoneElse() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);
        FrequentCustomer target = fc("Target", 1);

        @SuppressWarnings("unchecked")
        List<FrequentCustomer> bReferred =
                TestUtils.getField(b, "referredCustomers", List.class);
        bReferred.add(target);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                a.addReferredCustomer(target)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already referred"));
    }

    @Test
    void addReferredCustomer_returnsEarly_whenAlreadyContained_noRecursion() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        @SuppressWarnings("unchecked")
        List<FrequentCustomer> referred =
                TestUtils.getField(a, "referredCustomers", List.class);
        referred.add(b);

        assertDoesNotThrow(() -> a.addReferredCustomer(b));
        assertEquals(1, referred.size());
        assertSame(b, referred.get(0));
    }

    @Test
    void addReferrer_throws_whenReferrerAlreadySet() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);
        FrequentCustomer c = fc("Carol", 2);

        setReferrerDirect(a, b);

        assertThrows(ValidationException.class, () -> a.addReferrer(c));
    }

    @Test
    void removeReferredCustomer_throws_whenNull() {
        FrequentCustomer a = fc("Alice", 1);
        assertThrows(ValidationException.class, () -> a.removeReferredCustomer(null));
    }

    @Test
    void removeReferrer_throws_whenNull() {
        FrequentCustomer a = fc("Alice", 1);
        assertThrows(ValidationException.class, () -> a.removeReferrer(null));
    }

    @Test
    void setReferrer_throws_whenAnyArgumentNull() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        assertThrows(ValidationException.class, () -> a.setReferrer(null, b));
        assertThrows(ValidationException.class, () -> a.setReferrer(b, null));
    }

    @Test
    void setReferredCustomer_throws_whenAnyArgumentNull() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        assertThrows(ValidationException.class, () -> a.setReferredCustomer(null, b));
        assertThrows(ValidationException.class, () -> a.setReferredCustomer(b, null));
    }

    @Test
    void addReferredCustomer_currentlyCausesStackOverflow_dueToMutualRecursionBug() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        assertThrows(StackOverflowError.class, () -> a.addReferredCustomer(b));
    }

    @Test
    void addReferrer_currentlyCausesStackOverflow_dueToMutualRecursionBug() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        assertThrows(StackOverflowError.class, () -> a.addReferrer(b));
    }

    @Test
    void removeReferredCustomer_currentlyCausesStackOverflow_whenPresent_dueToMutualRecursionBug() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        @SuppressWarnings("unchecked")
        List<FrequentCustomer> referred =
                TestUtils.getField(a, "referredCustomers", List.class);
        referred.add(b);

        assertThrows(StackOverflowError.class, () -> a.removeReferredCustomer(b));
    }

    @Test
    void removeReferrer_currentlyCausesStackOverflow_whenMatches_dueToMutualRecursionBug() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer b = fc("Bob", 3);

        setReferrerDirect(a, b);

        assertThrows(StackOverflowError.class, () -> a.removeReferrer(b));
    }

    @Test
    void setReferrer_currentlyCausesStackOverflow_whenRefMatches_dueToMutualRecursionBug() {
        FrequentCustomer a = fc("Alice", 10);
        FrequentCustomer oldRef = fc("OldRef", 1);
        FrequentCustomer newRef = fc("NewRef", 2);

        setReferrerDirect(a, oldRef);

        assertThrows(StackOverflowError.class, () -> a.setReferrer(oldRef, newRef));
    }

    private void setReferrerDirect(FrequentCustomer target, FrequentCustomer referrer) {
        try {
            var f = FrequentCustomer.class.getDeclaredField("referrer");
            f.setAccessible(true);
            f.set(target, referrer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
