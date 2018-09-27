package homebound.common;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.json.*;

import homebound.entity.Item;

public class ResponseHelper {
	public static void createResponse(HttpServletResponse response, Collection<Item> itemList) {
		List<JSONObject> list = new ArrayList<>();
		try {
			for (Item item : itemList) {
				JSONObject obj = item.toJSONObject();
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray array = new JSONArray(list);
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(array);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getResponse(HttpServletResponse response, JSONObject jsonObject) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(jsonObject);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
