package de.neuland.pug4j.expression;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.parser.node.BlockNode;
import de.neuland.pug4j.template.PugTemplate;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RhinoExpressionHandlerTest {

    private RhinoExpressionHandler jsExpressionHandler;
    private PugModel pugModel;

    @Before
    public void setUp() throws Exception {
        jsExpressionHandler = new RhinoExpressionHandler();
        pugModel = new PugModel(new HashMap<String, Object>());
    }

    @Test
    public void evaluateBooleanExpression() throws Exception {

        Boolean aBoolean = jsExpressionHandler.evaluateBooleanExpression("1<5", pugModel);
        assertTrue(aBoolean);
    }

    @Test
    public void evaluateExpression() throws Exception {
        Object object = jsExpressionHandler.evaluateExpression("1<5", pugModel);
        assertTrue((Boolean) object);
    }

    @Test
    public void testArrayList() throws ExpressionException {
        jsExpressionHandler.evaluateExpression("var list = [1,2,3]", pugModel);
        int[] list = (int[]) pugModel.get("list");
        assertEquals(1,list[0]);
    }

    @Test
    public void testNull() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var list;", pugModel);
        Object list = pugModel.get("list");
        assertNull(list);
    }

    @Test
    public void testMap() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var map = {'foo':'bar'}", pugModel);
        Map map = (Map) pugModel.get("map");
        assertEquals("bar",map.get("foo"));

    }
    @Test
    public void testMapMulti() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var map = {" +
                "  'text': 'text'," +
                "  'image': 'image.jpg'," +
                "  'button' : {" +
                "  'text': 'textbutton'" +
                "  }," +
                "  \"list\": [1,2,3,4]" +
                "}", pugModel);
        Map map = (Map) pugModel.get("map");
        assertEquals("textbutton",((Map)map.get("button")).get("text"));

    }
    @Test
    public void testReturn() throws ExpressionException  {
        Object value = jsExpressionHandler.evaluateExpression("{" +
                "  'text': 'text'," +
                "  'image': 'image.jpg'," +
                "  'button' : {" +
                "  'text': 'textbutton'" +
                "  }," +
                "  \"list\": [1,2,3,4]" +
                "}", pugModel);

        assertEquals("textbutton",((Map)((Map)value).get("button")).get("text"));

    }

    @Test
    public void testInt() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var count = 5", pugModel);
        int count = (int) pugModel.get("count");
        assertEquals(5,count);

    }
    @Test
    public void testDoubleModel() throws ExpressionException  {
        pugModel.put("one",1.0);
        jsExpressionHandler.evaluateExpression("var count = one", pugModel);
        Object count = pugModel.get("count");
        assertEquals("1.0",count.toString());
    }
    @Test
    public void testDouble() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var price = 5.50", pugModel);
        Double price = (Double) pugModel.get("price");
        assertEquals(5.5,price,0.0001);
        assertEquals("5.5",price.toString());
    }
    @Test
    public void testDouble2() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var price = 5.00", pugModel);
        Object price = (Object) pugModel.get("price");
        assertEquals("5.0",price.toString());
    }
    @Test
    public void testString() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var moin = 'Hallo Welt!'", pugModel);
        String moin = (String) pugModel.get("moin");
        assertEquals("Hallo Welt!",moin);
    }
    @Test
    public void testBoolean() throws ExpressionException  {
        jsExpressionHandler.evaluateExpression("var what = true", pugModel);
        Boolean what = (Boolean) pugModel.get("what");
        assertTrue(what);
    }
    @Test
    public void testArray() throws ExpressionException  {
        Object[] o = (Object[])jsExpressionHandler.evaluateExpression("([])", pugModel);
        assertTrue(o.length==0);
    }
    @Test
    public void testArrayAccess() throws ExpressionException  {
        HashMap<String, Object> product = new HashMap<>();
        List images = new ArrayList();
        images.add("Image 1");
        images.add("Image 2");
        product.put("images", images);
        pugModel.put("product", product);
        Object o = jsExpressionHandler.evaluateExpression("(product.images[0])", pugModel);

        Object what = pugModel.get("x");
    }
    @Test
    public void testBlockNodeAccess() throws ExpressionException  {
        IndentWriter writer = new IndentWriter(new StringWriter());
        pugModel.put("pug4j__block", new BlockNode());
        pugModel.put("pug4j__writer", writer);
        pugModel.put("pug4j__template", new PugTemplate());
        pugModel.put("pug4j__model", new PugModel(new HashMap<>()));
        Object o = jsExpressionHandler.evaluateExpression("pug4j__block.execute(pug4j__writer,pug4j__model,pug4j__template)", pugModel);

        Object what = pugModel.get("x");
    }


}