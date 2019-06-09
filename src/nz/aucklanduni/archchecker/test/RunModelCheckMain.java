package nz.aucklanduni.archchecker.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import nz.aucklanduni.archchecker.model.ADLRequest;
import nz.aucklanduni.archchecker.model.ADLResult;

public class RunModelCheckMain {

	// private static String modName = "activiti";

	public static void checkModel(String modName, PrintWriter out) {
		HttpClient httpClient = HttpClientBuilder.create().build();

		HttpPost request = new HttpPost("http://localhost:53979/api/adlapi/verify");

		// query only test file with name xxx-test*
		File dir = new File("output");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(modName + "-test") && name.contains(".adl");
			}
		});
		try {
			for (File file : files) {
				String submodName = file.getName().substring(0, file.getName().indexOf("."));

				// read adl file
				String adlCode = new String(Files.readAllBytes(Paths.get("output/" + submodName + ".adl")),
						StandardCharsets.UTF_8);
				ADLRequest req = new ADLRequest();
				req.setModel(modName);
				req.setCode(adlCode);

				Gson gson = new GsonBuilder().create();
				String reqStr = gson.toJson(req);
				System.out.println(reqStr);

				StringEntity params = new StringEntity(reqStr);
				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				String resString = EntityUtils.toString(entity, "UTF-8");
				// System.out.println(resString);

				Type listType = new TypeToken<ArrayList<ADLResult>>() {
				}.getType();
				List<ADLResult> rslist = gson.fromJson(resString, listType);
				// System.out.println(list.get(1).getFullResultString());
				out.print(rslist.get(1).getModel());
				StringBuffer fullResult = new StringBuffer();
				for (ADLResult result : rslist) {
					out.print("," + result.getSmell() + "," + result.getResult() + "," + result.getVisitedStates() + ","
							+ result.getVerificationTime());
					fullResult.append(","+result.getFullResultString());
				}
				// start appending full result
				out.print(fullResult.toString());
				// end appending full result
				out.println();

			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String[] modSets = { "activiti", "hibernate", "hsqldb", "log4j", "springbeans", "springwebmvc", "xerces","xwork" };
		try (PrintWriter out = new PrintWriter("result/resultPat.csv")) {
			for (String mod : modSets) {
				checkModel(mod,out);
			}
			out.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
