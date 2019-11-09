package tesl.rest.reader

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ImageCreatorHelperTest {

    @Test
    fun calculateColumnLengths() {
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(1, 4)).isEqualTo(listOf(1, 0, 0, 0))
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(2, 4)).isEqualTo(listOf(1, 1, 0, 0))
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(3, 4)).isEqualTo(listOf(1, 1, 1, 0))
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(4, 4)).isEqualTo(listOf(1, 1, 1, 1))
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(5, 4)).isEqualTo(listOf(2, 1, 1, 1))
        Assertions.assertThat(ImageCreatorHelper.calculateColumnLengths(25, 3)).isEqualTo(listOf(9, 8, 8))
    }
}