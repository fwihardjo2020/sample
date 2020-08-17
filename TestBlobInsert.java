package oracle.apps.setup;

import java.util.Map;

import oracle.adf.share.ADFContext;
import oracle.adf.share.security.ADFSecurityConfig;
import oracle.adf.share.security.SecurityEnv;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.client.Configuration;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransaction;
import oracle.jbo.server.ViewObjectImpl;


public class TestBlobInsert
{
    public static void main(String[] args)
    {
        ApplicationModule am = null;
        am = getAM("oracle.apps.setup.commonSetup.filters.FilterService.applicationModule.FilterAM", "FilterAMLocal");
        ViewObjectImpl vo = (ViewObjectImpl)am.findViewObject("FilterMigrationSeed");
        
        Object[] keyobjs = new Object[] {300000000504001L};
        Key key = new Key(keyobjs);
        Row[] rows = vo.findByKey(key, 1);
        Row row = rows[0];
        
        byte[] byteArray = new byte[] {87, 79, 87, 46, 46, 46};
            
        BlobDomain blob = new BlobDomain(byteArray);
        
        int idx = vo.getAttributeIndexOf("ImageFile");
        row.setAttribute(idx, blob);
        
        DBTransaction dbtrans = ((ApplicationModuleImpl)am).getDBTransaction();
        
        dbtrans.commit();        
    }
    
    private static ApplicationModule getAM(String amDefName, String confName)
    {
        
        // this is needed to disable the mid-tier security in OA layer
        System.setProperty("applseed-upload-mode", "true");
        
        Map env = (Map) ADFContext.getCurrent ().getADFConfig ().getConfigObject (ADFSecurityConfig.namespaceURI);
        if (env != null)
        {
            //LOGGER.finest ("Disabling ADF Security");
            env.put (SecurityEnv.PROP_AUTHENTICATION_REQUIRE, Boolean.FALSE.toString ());
            env.put (SecurityEnv.PROP_AUTHORIZATION_ENFORCE, Boolean.FALSE.toString ());
        }

        ApplicationModule am = null;
        am = Configuration.createRootApplicationModule (amDefName, confName);
        
        return am;

    }
}
