package Alignment.align;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("the algorithm is able to ")
class Smith_WatermanSpec {
    @DisplayName("split the string with [. ")
    @Test
    void splitTest(){
        String test = "sdkfksf kdjfk [fdfdf] ";
        assertEquals("sdkfksf kdjfk",test.split("\\u005B")[0]);
    }

}