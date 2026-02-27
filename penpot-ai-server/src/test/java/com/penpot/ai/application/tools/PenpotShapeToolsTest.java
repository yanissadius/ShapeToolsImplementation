package com.penpot.ai.application.tools;

import com.penpot.ai.application.tools.support.PenpotToolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PenpotShapeTools")
class PenpotShapeToolsTest {

    @Mock
    private PenpotToolExecutor toolExecutor;

    @InjectMocks
    private PenpotShapeTools penpotShapeTools;

    private static final String FAKE_UUID = "4bb78d46-7ac9-80f7-8007-8586c765544b";
    private static final String SHAPE_ID_1 = "aaaaaaaa-0000-0000-0000-000000000001";
    private static final String SHAPE_ID_2 = "bbbbbbbb-0000-0000-0000-000000000002";

    @BeforeEach
    void setUp() {
        when(toolExecutor.createShape(anyString(), anyString())).thenReturn(FAKE_UUID);
    }

    // =========================================================================
    // createRectangle
    // =========================================================================

    @Nested
    @DisplayName("createRectangle")
    class CreateRectangle {

        @Test
        @DisplayName("Nominal : JS correct et UUID retourné")
        void shouldGenerateCorrectJs() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            String result = penpotShapeTools.createRectangle(10, 20, 100, 50, "#FF0000", "rect");

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("rectangle"));
            assertThat(code.getValue())
                .contains("rect.x = 10")
                .contains("rect.y = 20")
                .contains("rect.resize(100, 50)")
                .contains("fillColor: '#FF0000'")
                .contains("rect.name = 'rect'")
                .contains("return rect.id");
            assertThat(result).isEqualTo(FAKE_UUID);
        }

        @Test
        @DisplayName("Sans paramètres optionnels : pas de fills ni de name")
        void shouldOmitOptionalFields() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createRectangle(0, 0, 100, 100, null, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("rectangle"));
            assertThat(code.getValue())
                .doesNotContain("fills")
                .doesNotContain("rect.name");
        }
    @Test
    @DisplayName("Rectangle crée avec le nom renseigné")
    void shouldIncludeName_WhencreateRectangle_withName() {
        //GIVEN
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        //WHEN
        penpotShapeTools.createRectangle(0, 0, 100, 100, null, "mon-rect");
        //THEN
        verify(toolExecutor).createShape(code.capture(), eq("rectangle"));
        assertThat(code.getValue()).contains("rect.name = 'mon-rect'");
}
    }

    // =========================================================================
    // createEllipse
    // =========================================================================

    @Nested
    @DisplayName("createEllipse")
    class CreateEllipse {

        @Test
        @DisplayName("Nominal : JS correct et UUID retourné")
        void shouldGenerateCorrectJs() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            String result = penpotShapeTools.createEllipse(50, 60, 200, 200, "#00FF00", "circle");

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("ellipse"));
            assertThat(code.getValue())
                .contains("ellipse.x = 50")
                .contains("ellipse.resize(200, 200)")
                .contains("fillColor: '#00FF00'")
                .contains("return ellipse.id");
            assertThat(result).isEqualTo(FAKE_UUID);
        }

        @Test
        @DisplayName("Sans paramètres optionnels : pas de fills ni de name")
        void shouldOmitOptionalFields() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createEllipse(0, 0, 100, 100, null, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("ellipse"));
            assertThat(code.getValue())
                .doesNotContain("fills")
                .doesNotContain("ellipse.name");
        }
        @Test
        @DisplayName("")
        void shouldOmitName_WhencreateBoard_withNullName() {
            //Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
            //When
            penpotShapeTools.createBoard(1080, 1080, null, "#FFFFFF");
            //Then
            verify(toolExecutor).createShape(code.capture(), eq("board"));
            assertThat(code.getValue())
                .doesNotContain("board.name")
                .contains("fillColor: '#FFFFFF'");
        }

    }

    // =========================================================================
    // createText
    // =========================================================================

    @Nested
    @DisplayName("createText")
    class CreateText {

        @Test
        @DisplayName("Nominal : délègue avec type 'text' et retourne UUID")
        void shouldDelegateWithCorrectType() {
            // When
            String result = penpotShapeTools.createText("SAUMON FUMÉ", 80, 60, 88, "bold", "#0D2137", "title");

            // Then
            verify(toolExecutor).createShape(anyString(), eq("text"));
            assertThat(result).isEqualTo(FAKE_UUID);
        }

        @Test
        @DisplayName("Paramètres null : ne plante pas")
        void shouldHandleNullParams() {
            // When / Then — ne doit pas lever d'exception
            penpotShapeTools.createText("Hello", 0, 0, null, null, null, null);
            verify(toolExecutor, times(1)).createShape(anyString(), eq("text"));
        }
    }

    // =========================================================================
    // createBoard
    // =========================================================================

    @Nested
    @DisplayName("createBoard")
    class CreateBoard {

        @Test
        @DisplayName("Nominal : JS correct avec dimensions et nom")
        void shouldGenerateCorrectJs() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createBoard(1080, 1080, "Instagram Post", "#FFFFFF");

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("board"));
            assertThat(code.getValue())
                .contains("board.resize(1080, 1080)")
                .contains("board.name = 'Instagram Post'")
                .contains("fillColor: '#FFFFFF'")
                .contains("return board.id");
        }

        @Test
        @DisplayName("Sans backgroundColor : pas de fills")
        void shouldOmitFillsWhenNoBackground() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createBoard(1000, 1414, "Poster A4", null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("board"));
            assertThat(code.getValue()).doesNotContain("fills");
        }
    }

    // =========================================================================
    // createStar
    // =========================================================================

    @Nested
    @DisplayName("createStar")
    class CreateStar {

        @Test
        @DisplayName("Nominal : génère un SVG path avec createShapeFromSvg")
        void shouldGenerateSvgPath() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createStar(100, 100, 100, 100, 5, 38, "#FFD700", "star");

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("star"));
            assertThat(code.getValue())
                .contains("createShapeFromSvg")
                .contains("<path")
                .contains("fill='#FFD700'")
                .contains("group.x = 100")
                .contains("return group.id");
        }

        @Test
        @DisplayName("Sans couleur : fallback #CCCCCC")
        void shouldUseDefaultColor() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createStar(0, 0, 100, 100, null, null, null, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("star"));
            assertThat(code.getValue()).contains("fill='#CCCCCC'");
        }
        @Test
        @DisplayName("")
        void shouldUseProvidedValues_WhencreateStar_withAllParams() {
            //Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
            //When
            penpotShapeTools.createStar(0, 0, 100, 100, 6, 50, "#FF0000", "etoile");
            //Then
            verify(toolExecutor).createShape(code.capture(), eq("star"));
            assertThat(code.getValue())
                .contains("fill='#FF0000'")
                .contains("group.name = 'etoile'");
}
    }

    // =========================================================================
    // createTriangle
    // =========================================================================

    @Nested
    @DisplayName("createTriangle")
    class CreateTriangle {

        @Test
        @DisplayName("Equilatéral : apex centré M cx,0")
        void shouldGenerateEquilateralPath() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createTriangle(0, 0, 100, 100, "equilateral", "#E74C3C", null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("triangle"));
            assertThat(code.getValue())
                .contains("M 50.0,0")
                .contains("createShapeFromSvg")
                .contains("fill='#E74C3C'");
        }

        @Test
        @DisplayName("Right : angle droit M 0,0")
        void shouldGenerateRightPath() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createTriangle(0, 0, 100, 100, "right", null, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("triangle"));
            assertThat(code.getValue())
                .contains("M 0,0")
                .doesNotContain("M 50");
        }

        @Test
        @DisplayName("Type null : fallback equilateral")
        void shouldFallbackToEquilateral() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createTriangle(0, 0, 100, 100, null, null, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("triangle"));
            assertThat(code.getValue()).contains("M 50.0,0");
        }

        @Test
        @DisplayName("Isosceles : apex centré comme equilateral")
        void shouldGenerateIsoscelesPath() {
        // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

        // When
             penpotShapeTools.createTriangle(0, 0, 100, 60, "isosceles", null, null);

        // Then
            verify(toolExecutor).createShape(code.capture(), eq("triangle"));
            assertThat(code.getValue()).contains("M 50.0,0");
}
    @Test
    @DisplayName("createTriangle : avec nom, group.name présent dans le JS")
    void createTriangle_shouldIncludeNameWhenProvided() {
    // Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

    // When
        penpotShapeTools.createTriangle(0, 0, 100, 100, "equilateral", null, "mon-triangle");

    // Then
        verify(toolExecutor).createShape(code.capture(), eq("triangle"));
        assertThat(code.getValue()).contains("group.name = 'mon-triangle'");
}
    }


    // =========================================================================
    // createBoolean
    // =========================================================================

    @Nested
    @DisplayName("createBoolean")
    class CreateBoolean {

        @Test
        @DisplayName("Nominal : JS correct avec union et 2 shapes")
        void shouldGenerateCorrectJs() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createBoolean("union", SHAPE_ID_1 + "," + SHAPE_ID_2, "résultat");

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue())
                .contains("getShapeById('" + SHAPE_ID_1 + "')")
                .contains("getShapeById('" + SHAPE_ID_2 + "')")
                .contains("penpot.createBoolean('union', shapes)")
                .contains("result.name = 'résultat'")
                .contains("return result.id");
        }

        @Test
        @DisplayName("subtract → difference dans l'API Penpot")
        void shouldMapSubtractToDifference() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createBoolean("subtract", SHAPE_ID_1 + "," + SHAPE_ID_2, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue())
                .contains("createBoolean('difference'")
                .doesNotContain("createBoolean('subtract'");
        }

        @Test
        @DisplayName("Type inconnu : fallback union")
        void shouldFallbackToUnion() {
            // Given
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

            // When
            penpotShapeTools.createBoolean("invalid", SHAPE_ID_1 + "," + SHAPE_ID_2, null);

            // Then
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue()).contains("createBoolean('union'");
        }

        @Test
        @DisplayName("difference, intersection, exclude : mappés correctement")
        void shouldMapAllBooleanTypes() {
        // difference

            //when
            penpotShapeTools.createBoolean("difference", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
            ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
            // then
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue()).contains("createBoolean('difference'");

            reset(toolExecutor);
            when(toolExecutor.createShape(anyString(), anyString())).thenReturn(FAKE_UUID);

    // intersection
            penpotShapeTools.createBoolean("intersection", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
            code = ArgumentCaptor.forClass(String.class);
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue()).contains("createBoolean('intersection'");

            reset(toolExecutor);
            when(toolExecutor.createShape(anyString(), anyString())).thenReturn(FAKE_UUID);

    // exclude
            penpotShapeTools.createBoolean("exclude", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
            code = ArgumentCaptor.forClass(String.class);
            verify(toolExecutor).createShape(code.capture(), eq("boolean"));
            assertThat(code.getValue()).contains("createBoolean('exclude'");
}
    @Test
    @DisplayName("Sans nom : pas de board.name dans le JS")
    void shouldOmitNameWhenNull() {
    // Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);

    // When
        penpotShapeTools.createBoard(1080, 1080, null, null);

    // Then
        verify(toolExecutor).createShape(code.capture(), eq("board"));
        assertThat(code.getValue()).doesNotContain("board.name");
}

    @Test
    void shouldFallbackToUnion_WhencreateBoolean_withNullType() {
        //Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        //When
        penpotShapeTools.createBoolean(null, SHAPE_ID_1 + "," + SHAPE_ID_2, null);
        //Then
        verify(toolExecutor).createShape(code.capture(), eq("boolean"));
        assertThat(code.getValue()).contains("createBoolean('union'");
}

    @Test
    void shouldMapToDifference_WhencreateBoolean_withDifferenceType() {
        //Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        //When
        penpotShapeTools.createBoolean("difference", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
        //Then
        verify(toolExecutor).createShape(code.capture(), eq("boolean"));
        assertThat(code.getValue()).contains("createBoolean('difference'");
}

    @Test
    void shouldMapToIntersection_WhencreateBoolean_withIntersectType() {
        //Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        //When
        penpotShapeTools.createBoolean("intersect", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
        //Then
        verify(toolExecutor).createShape(code.capture(), eq("boolean"));
        assertThat(code.getValue()).contains("createBoolean('intersection'");
}

    @Test
    void shouldMapToExclude_WhencreateBoolean_withExcludeType() {
        //Given
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        //When
        penpotShapeTools.createBoolean("exclude", SHAPE_ID_1 + "," + SHAPE_ID_2, null);
        //Then
        verify(toolExecutor).createShape(code.capture(), eq("boolean"));
        assertThat(code.getValue()).contains("createBoolean('exclude'");
}
    }
}