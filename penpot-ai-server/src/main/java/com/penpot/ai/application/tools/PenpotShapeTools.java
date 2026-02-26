package com.penpot.ai.application.tools;

import com.penpot.ai.application.tools.support.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Locale;

/**
 * Tools pour la création de formes graphiques dans Penpot.
 *
 * <p>L'exécution et le formatage sont délégués à {@link PenpotToolExecutor}.</p>
 * <p>La génération de texte réutilise {@link PenpotJsSnippets#createText}.</p>
 *
 * @see PenpotLayoutTools pour l'alignement
 * @see PenpotTransformTools pour les transformations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PenpotShapeTools {

    private final PenpotToolExecutor toolExecutor;

    @Tool(description = """
        Create a rectangle shape in Penpot.

        CRITICAL: This tool returns a UUID that you MUST use in subsequent operations.
        Save this ID immediately after receiving it!

        Examples:
        - "Create a red rectangle 100x50"
        - "Add a blue box at position (10, 20)"

        Returns the UUID of the created shape that you must save for later use.
        """)
    public String createRectangle(
        @ToolParam(description = "X position in pixels") Integer x,
        @ToolParam(description = "Y position in pixels") Integer y,
        @ToolParam(description = "Width in pixels") Integer width,
        @ToolParam(description = "Height in pixels") Integer height,
        @ToolParam(description = "Fill color in hex format (#RRGGBB)", required = false) String fillColor,
        @ToolParam(description = "Optional name for the rectangle", required = false) String name
    ) {
        log.info("Tool called: createRectangle (x={}, y={}, w={}, h={}, color={})", x, y, width, height, fillColor);
        return toolExecutor.createShape(buildRectangleCode(x, y, width, height, fillColor, name), "rectangle");
    }

    @Tool(description = """
        Create an ellipse (circle or oval) shape in Penpot.

        CRITICAL: Returns a UUID that you MUST save for later operations!

        For a perfect circle, use the same width and height.
        """)
    public String createEllipse(
        @ToolParam(description = "X position of center in pixels") Integer x,
        @ToolParam(description = "Y position of center in pixels") Integer y,
        @ToolParam(description = "Width in pixels") Integer width,
        @ToolParam(description = "Height in pixels (same as width for circle)") Integer height,
        @ToolParam(description = "Fill color in hex format", required = false) String fillColor,
        @ToolParam(description = "Optional name for the ellipse", required = false) String name
    ) {
        log.info("Tool called: createEllipse (x={}, y={}, w={}, h={})", x, y, width, height);
        return toolExecutor.createShape(buildEllipseCode(x, y, width, height, fillColor, name), "ellipse");
    }

    @Tool(description = """
        Create a text element in Penpot.

        CRITICAL: Returns a UUID that you MUST save!

        Font sizes: small=14, medium=18, large=24, xlarge=36, xxlarge=48
        Font weights: normal, bold
        """)
    public String createText(
        @ToolParam(description = "Text content to display") String content,
        @ToolParam(description = "X position in pixels") Integer x,
        @ToolParam(description = "Y position in pixels") Integer y,
        @ToolParam(description = "Font size in pixels (default: 16)", required = false) Integer fontSize,
        @ToolParam(description = "Font weight: normal or bold (default: normal)", required = false) String fontWeight,
        @ToolParam(description = "Text color in hex format (default: #000000)", required = false) String fillColor,
        @ToolParam(description = "Optional name for the text element", required = false) String name
    ) {
        log.info("Tool called: createText (content='{}', x={}, y={})", content, x, y);
        return toolExecutor.createShape(
            PenpotJsSnippets.createText(content, x, y, fontSize, fontWeight, fillColor, name),
            "text"
        );
    }

    @Tool(description = """
        Create a board (artboard/canvas) in Penpot.
        Use this as a container for design elements.

        CRITICAL: Returns a UUID that you MUST save!

        Common sizes:
        - Social media post: 1080x1080
        - Instagram story: 1080x1920
        - A4 portrait: 2480x3508 (at 300dpi)
        - Email: 600x1200
        """)
    public String createBoard(
        @ToolParam(description = "Board width in pixels") Integer width,
        @ToolParam(description = "Board height in pixels") Integer height,
        @ToolParam(description = "Name of the board") String name,
        @ToolParam(description = "Background color in hex format", required = false) String backgroundColor
    ) {
        log.info("Tool called: createBoard (w={}, h={}, name='{}')", width, height, name);
        return toolExecutor.createShape(buildBoardCode(width, height, name, backgroundColor), "board");
    }

    @Tool(description = """
        Create a star shape in Penpot.

        CRITICAL: Returns a UUID that you MUST save!

        Default is 5 points with 38% inner radius.
        """)
    public String createStar(
        @ToolParam(description = "X position in pixels") Integer x,
        @ToolParam(description = "Y position in pixels") Integer y,
        @ToolParam(description = "Width in pixels") Integer width,
        @ToolParam(description = "Height in pixels") Integer height,
        @ToolParam(description = "Number of points (default: 5)", required = false) Integer points,
        @ToolParam(description = "Inner radius percentage 0-100 (default: 38)", required = false) Integer innerRadius,
        @ToolParam(description = "Fill color in hex format", required = false) String fillColor,
        @ToolParam(description = "Optional name for the star", required = false) String name
    ) {
        log.info("Tool called: createStar (x={}, y={}, w={}, h={}, points={})", x, y, width, height, points);
        return toolExecutor.createShape(buildStarCode(x, y, width, height, points, innerRadius, fillColor, name), "star");
    }

    // Tools pour la création d'un triangle (isoscele , carré , equilateral)
    
    @Tool(description = """
        Create a triangle shape in Penpot.

        CRITICAL: Returns a UUID that you MUST save for later operations!

        Triangle types:
        - equilateral : apex at top-center, equal sides (default)
        - right       : right angle at bottom-left
        - isosceles   : apex at top-center, base wider than height

        Examples:
        - "Create a red equilateral triangle 100x100"
        - "Add a right triangle at position (50, 50)"
        """)
    public String createTriangle(
        @ToolParam(description = "X position in pixels") Integer x,
        @ToolParam(description = "Y position in pixels") Integer y,
        @ToolParam(description = "Width in pixels") Integer width,
        @ToolParam(description = "Height in pixels") Integer height,
        @ToolParam(description = "Triangle type: equilateral, right, isosceles (default: equilateral)", required = false) String type,
        @ToolParam(description = "Fill color in hex format (#RRGGBB)", required = false) String fillColor,
        @ToolParam(description = "Optional name for the triangle", required = false) String name
    ) {
        log.info("Tool called: createTriangle (x={}, y={}, w={}, h={}, type={})",
                x, y, width, height, type);
        return toolExecutor.createShape(
                buildTriangleCode(x, y, width, height, type, fillColor, name),
                "triangle"
        );
    }
    // Tool pour la création d'un Boolean (union , intersection , difference entre shapes )
    @Tool(description = """
        Combine multiple existing shapes using a boolean operation in Penpot.

        CRITICAL: You must provide the UUIDs of existing shapes (returned by previous tool calls).
                  Never invent fake IDs like "shape1", "rect1", etc.

        Boolean operations:
        - union      : merges all shapes into one combined shape
        - subtract   : removes the area of subsequent shapes from the first shape
        - intersect  : keeps only the overlapping area between shapes
        - exclude    : keeps non-overlapping areas, removes overlap

        Requires at least 2 shape IDs.

        Example usage:
        - "Combine these two circles into one" → union
        - "Cut a hole in the rectangle using the circle" → subtract
        - "Keep only the overlapping part" → intersect

        Returns the UUID of the resulting boolean shape.
        """)
    public String createBoolean(
        @ToolParam(description = "Boolean operation type: union, subtract, intersect, or exclude") String boolType,
        @ToolParam(description = "Comma-separated UUIDs of the shapes to combine (minimum 2), e.g: 'uuid1,uuid2,uuid3'") String shapeIds,
        @ToolParam(description = "Optional name for the resulting boolean shape", required = false) String name
    ) {
        log.info("Tool called: createBoolean (type={}, shapeIds={})", boolType, shapeIds);
        return toolExecutor.createShape(buildBooleanCode(boolType, shapeIds, name), "boolean");
    }
    

    // ==================== CODE GENERATION METHODS ====================

    private String buildRectangleCode(Integer x, Integer y, Integer width, Integer height, String fillColor, String name) {
        StringBuilder code = new StringBuilder();
        code.append("const rect = penpot.createRectangle();\n");
        code.append(String.format("rect.x = %d;\n", x));
        code.append(String.format("rect.y = %d;\n", y));
        code.append(String.format("rect.resize(%d, %d);\n", width, height));
        if (fillColor != null && !fillColor.isBlank())
            code.append(String.format("rect.fills = [{ fillColor: '%s' }];\n", fillColor));
        if (name != null && !name.isBlank())
            code.append(String.format("rect.name = '%s';\n", PenpotJsSnippets.escapeJsString(name)));
        code.append("return rect.id;\n");
        return code.toString();
    }

    private String buildEllipseCode(Integer x, Integer y, Integer width, Integer height, String fillColor, String name) {
        StringBuilder code = new StringBuilder();
        code.append("const ellipse = penpot.createEllipse();\n");
        code.append(String.format("ellipse.x = %d;\n", x));
        code.append(String.format("ellipse.y = %d;\n", y));
        code.append(String.format("ellipse.resize(%d, %d);\n", width, height));
        if (fillColor != null && !fillColor.isBlank())
            code.append(String.format("ellipse.fills = [{ fillColor: '%s' }];\n", fillColor));
        if (name != null && !name.isBlank())
            code.append(String.format("ellipse.name = '%s';\n", PenpotJsSnippets.escapeJsString(name)));
        code.append("return ellipse.id;\n");
        return code.toString();
    }

    private String buildBoardCode(Integer width, Integer height, String name, String backgroundColor) {
        StringBuilder code = new StringBuilder();
        code.append("const board = penpot.createBoard();\n");
        code.append(String.format("board.resize(%d, %d);\n", width, height));
        if (name != null && !name.isBlank())
            code.append(String.format("board.name = '%s';\n", PenpotJsSnippets.escapeJsString(name)));
        if (backgroundColor != null && !backgroundColor.isBlank())
            code.append(String.format("board.fills = [{ fillColor: '%s' }];\n", backgroundColor));
        code.append("return board.id;\n");
        return code.toString();
    }

    private String buildStarCode(
        Integer x, Integer y, Integer width, Integer height,
        Integer points, Integer innerRadius, String fillColor, String name
    ) {
        int actualPoints = (points != null && points > 2) ? points : 5;
        double ratio = (innerRadius != null && innerRadius > 0 && innerRadius < 100)
            ? innerRadius / 100.0 : 0.382;

        String pathData = generateStarPath(width, height, actualPoints, ratio);
        String color = (fillColor != null && !fillColor.isBlank()) ? fillColor : "#CCCCCC";
        String svg = String.format(
            "<svg width='%d' height='%d' viewBox='0 0 %d %d' xmlns='http://www.w3.org/2000/svg'>"
            + "<path d='%s' fill='%s'/></svg>",
            width, height, width, height, pathData, color
        );

        StringBuilder code = new StringBuilder();
        code.append(String.format("const svg = `%s`;\n", svg));
        code.append("const group = penpot.createShapeFromSvg(svg);\n");
        code.append("if (!group) throw new Error('Failed to create star from SVG');\n");
        code.append(String.format("group.x = %d;\n", x));
        code.append(String.format("group.y = %d;\n", y));
        if (name != null && !name.isBlank())
            code.append(String.format("group.name = '%s';\n", PenpotJsSnippets.escapeJsString(name)));
        code.append("return group.id;\n");
        return code.toString();
    }

    private String generateStarPath(int width, int height, int points, double innerRadiusRatio) {
        double cx = width / 2.0;
        double cy = height / 2.0;
        double rx = width / 2.0;
        double ry = height / 2.0;

        StringBuilder sb = new StringBuilder();
        double step = Math.PI / points;
        double angle = -Math.PI / 2;

        for (int i = 0; i < 2 * points; i++) {
            double r = (i % 2 == 0) ? 1.0 : innerRadiusRatio;
            double currX = cx + Math.cos(angle) * rx * r;
            double currY = cy + Math.sin(angle) * ry * r;
            if (i == 0) sb.append("M").append(currX).append(" ").append(currY);
            else        sb.append(" L").append(currX).append(" ").append(currY);
            angle += step;
        }
        sb.append(" Z");
        return sb.toString();
    }

    private String buildTriangleCode(
            Integer x, Integer y, Integer width, Integer height,
            String type, String fillColor, String name) {

        String pathData = generateTrianglePath(width, height, type);
        String color = (fillColor != null && !fillColor.isBlank()) ? fillColor : "#CCCCCC";

        // SVG embarqué dans le JS — même pattern que buildStarCode
        String svg = String.format(
                "<svg width='%d' height='%d' viewBox='0 0 %d %d' xmlns='http://www.w3.org/2000/svg'>"
                + "<path d='%s' fill='%s'/></svg>",
                width, height, width, height, pathData, color
        );

        StringBuilder code = new StringBuilder();
        code.append(String.format("const svg = `%s`;\n", svg));
        code.append("const group = penpot.createShapeFromSvg(svg);\n");
        code.append("if (!group) throw new Error('Failed to create triangle from SVG');\n");
        code.append(String.format("group.x = %d;\n", x));
        code.append(String.format("group.y = %d;\n", y));
        if (name != null && !name.isBlank()) {
            code.append(String.format("group.name = '%s';\n", PenpotJsSnippets.escapeJsString(name)));
        }
        code.append("return group.id;\n");
        return code.toString();
    }

    /**
     * Génère le SVG path selon le type de triangle.
     *
     * equilateral 
     * right     
     * isosceles   
     *               
     */
    private String generateTrianglePath(int width, int height, String type) {
    double cx = width / 2.0;
    if (type == null) type = "equilateral";

    return switch (type.toLowerCase().trim()) {
        case "right" ->
            String.format("M 0,0 L %d,%d L 0,%d Z", width, height, height);

        case "isosceles" ->
            String.format(Locale.US, "M %.1f,0 L %d,%d L 0,%d Z", cx, width, height, height);

        default -> // equilateral
            String.format(Locale.US, "M %.1f,0 L %d,%d L 0,%d Z", cx, width, height, height);
    };
}

     /**
     * Génère le JS pour une opération booléenne Penpot.
     *
     */
    private String buildBooleanCode(String boolType, String shapeIds, String name) {
        String resolvedType = resolveBooleanType(boolType);
        String[] ids = shapeIds.split(",");

        StringBuilder code = new StringBuilder();

        // Récupération des shapes existantes depuis leurs UUIDs
        code.append("const shapes = [];\n");
        for (String id : ids) {
            String trimmedId = id.trim();
            code.append(String.format(
                "const shape_%s = penpot.currentPage.getShapeById('%s');\n",
                trimmedId.replace("-", "_"), trimmedId
            ));
            code.append(String.format(
                "if (!shape_%s) throw new Error('Shape not found: %s');\n",
                trimmedId.replace("-", "_"), trimmedId
            ));
            code.append(String.format("shapes.push(shape_%s);\n",
                trimmedId.replace("-", "_")));
        }

        // Validation du nombre de shapes
        code.append("if (shapes.length < 2) throw new Error('createBoolean requires at least 2 shapes');\n");

        // Opération booléenne
        code.append(String.format(
            "const result = penpot.createBoolean('%s', shapes);\n", resolvedType
        ));
        code.append("if (!result) throw new Error('Boolean operation failed');\n");

        // Nom optionnel
        if (name != null && !name.isBlank()) {
            code.append(String.format("result.name = '%s';\n",
                PenpotJsSnippets.escapeJsString(name)));
        }

        code.append("return result.id;\n");
        return code.toString();
    }
     /**
     * Mappe le type booléen vers la valeur attendue par l'API Penpot.
     *
     * Penpot API accepte : 'union' | 'difference' | 'exclude' | 'intersection'
     * On accepte en plus : 'subtract' → 'difference', 'intersect' → 'intersection'
     */
    private String resolveBooleanType(String boolType) {
        if (boolType == null) return "union"; // fallback
        return switch (boolType.toLowerCase().trim()) {
            case "subtract", "difference" -> "difference";
            case "intersect", "intersection" -> "intersection";
            case "exclude"                 -> "exclude";
            case "union"                   -> "union";
            default                        -> "union"; 
        };
    }
}