package sample.com.tool;

import com.alibaba.fastjson.JSON;
import org.elastos.service.ElaDidService;
import org.elastos.POJO.DidEntity;

import java.util.*;


public class ElaDidDataTool {

    String appName = "";
    String appId = "";
    String addIdSign = "";
    String didMnemonic = "";
    String didPrivateKey = "";
    String didPublicKey = "";
    String did = "";
    String upload = "";

    String didNodeUrl = "http://54.64.220.165:21604";

    final String help = "--name         the app name.\n" +
            "--mne          mnemonic, if not set, will generate a new one.\n\n";

    ElaDidService didService = new ElaDidService(didNodeUrl, true);

    public void createAppInfoAndDid(String inAppName, String inMnemonic) throws Exception {

        if (inMnemonic.isEmpty()) {
            didMnemonic = didService.createMnemonic();
        } else {
            didMnemonic = inMnemonic;
        }

        String ret = didService.createDid(didMnemonic, 0);
        Map data = JSON.parseObject(ret, Map.class);
        didPrivateKey = (String) data.get("DidPrivateKey");
        did = (String) data.get("Did");
        didPublicKey = (String) data.get("DidPublicKey");

        appName = inAppName;
        appId = didService.signMessage(didPrivateKey, appName);
        addIdSign = didService.signMessage(didPrivateKey, appId);


        List<DidEntity.DidProperty> properties = new ArrayList<>();

        if (inMnemonic.isEmpty()) {
            DidEntity.DidProperty pubkeyProperty = new DidEntity.DidProperty();
            pubkeyProperty.setKey("PublicKey");
            pubkeyProperty.setValue(didPublicKey);
            properties.add(pubkeyProperty);
        }

        DidEntity.DidProperty appIDProperty = new DidEntity.DidProperty();
        appIDProperty.setKey("Dev/" + appName + "/AppID");
        appIDProperty.setValue(appId);
        properties.add(appIDProperty);

        DidEntity didEntity = new DidEntity();
        didEntity.setProperties(properties);
        didEntity.setDid(did);

        upload = JSON.toJSONString(didEntity);
    }

    public void showData(){
        System.out.println("appName: " + appName);
        System.out.println("appId: "+appId);
        System.out.println("appIdSign: "+addIdSign);
        System.out.println("didMnemonic: " + didMnemonic);
        System.out.println("didPrivateKey: "+didPrivateKey);
        System.out.println("didPublicKey: "+didPublicKey);
        System.out.println("did: "+did);
        System.out.println("upload data: "+upload);
    }

    public static void main(String[] args) throws Exception {
        List<String> params = new ArrayList<>();
        params.addAll(Arrays.asList(args));

        if (params.isEmpty()) {
            System.out.println("Should add app name in parameter");
            return;
        }

        String appName = "";
        String mnemonic = "";

        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            if (param.equals("--h") || param.equals("--help")) {
                System.out.println("Should add app name in parameter");
            } else if (param.equals("--name")) {
                i += 1;
                appName = params.get(i);
            } else if (param.equals("--mne")) {
                i += 1;
                int j = i;
                for (; j < i + 11; j++) {
                    mnemonic += params.get(j) + " ";
                }
                mnemonic += params.get(j);
                i = j;
            } else {
                System.out.println("not support");
                return;
            }

        }

        if (appName.isEmpty()) {
            System.out.println("Should add app name in parameter");
            return;
        }

        ElaDidDataTool tool = new ElaDidDataTool();
        tool.createAppInfoAndDid(appName, mnemonic);
        tool.showData();
    }

}
