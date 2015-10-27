import com.apps.darkone.redpitayascope.parameters.ParameterManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ParameterManagerTest {

    private static final String APP_NAME_TEST = "test_app";
    private static final String APP_PARAM_NAME_TEST = "test_param";


    @Test
    public void parameterAddNewApplicationParameters_Test() {
        ParameterManager param = new ParameterManager();
        param.addNewAppsParams(APP_NAME_TEST);
        assertTrue(param.isParamsAlreadyPresent(APP_NAME_TEST));
    }

    @Test
    public void parameterAddNewParameter_Test() {
        ParameterManager param = new ParameterManager();
        param.addNewAppsParams(APP_NAME_TEST);

        param.addParameter(APP_NAME_TEST, APP_PARAM_NAME_TEST, 1.0, false);
        assertFalse(param.getParamList(APP_NAME_TEST).isEmpty());
    }

    @Test
    public void parameterCompareList_Test() {
        ParameterManager param = new ParameterManager();
        param.addNewAppsParams(APP_NAME_TEST);

        ParameterManager paramTest = new ParameterManager();
        paramTest.addNewAppsParams(APP_NAME_TEST);

        ParameterManager paramTestNoChange = new ParameterManager();
        paramTestNoChange.addNewAppsParams(APP_NAME_TEST);

        // Add a parameter
        param.addParameter(APP_NAME_TEST, APP_PARAM_NAME_TEST, 1.0, false);
        paramTestNoChange.addParameter(APP_NAME_TEST, APP_PARAM_NAME_TEST, 1.0, false);
        paramTest.addParameter(APP_NAME_TEST, APP_PARAM_NAME_TEST, 2.0, false);


        assertTrue(paramTest.getParamList(APP_NAME_TEST).equals(param.getParamList(APP_NAME_TEST)));
        assertFalse(paramTestNoChange.equals(param.getParamList(APP_NAME_TEST)));


    }

}