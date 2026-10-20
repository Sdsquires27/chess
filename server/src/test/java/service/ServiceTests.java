package service;

import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

abstract class ServiceTests {

    FakeDataAccess dataAccess = new FakeDataAccess();

    @BeforeEach
    public void resetAccess() {
        dataAccess.resetAccess();
    }

    void assertMethodsAccessed(String[] methods) {
        for (var method : dataAccess.methods) {
            if (Arrays.asList(methods).contains(method)) {
                assert(dataAccess.calledMethod.get(method));
                continue;
            }
            assert(!dataAccess.calledMethod.get(method));
        }
    }
}
