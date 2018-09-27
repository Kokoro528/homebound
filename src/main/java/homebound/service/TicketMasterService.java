package homebound.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import homebound.algorithm.GeoRecommendation;
import homebound.common.GeoHash;
import homebound.dao.AbstractDao;
import homebound.entity.HistoryItem;
import homebound.entity.Item;
import homebound.entity.Item.*;



@Service
public class TicketMasterService {
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;


	@Autowired
	private AbstractDao dao;
	
	@Autowired
	private GeoRecommendation recommendation;


	private static String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static String API_KEY = "DhHtAnTAn4A81blxRdH2CRNFH4QgrxCX";
	private static int PRECISION = 8;

	public List<Item> search(double latitude, double longitude, String term) {

		String geoHash = GeoHash.encodeGeohash(latitude, longitude, PRECISION);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL).queryParam("apikey", API_KEY)
				.queryParam("geoPoint", geoHash).queryParam("radius", "100");
		if (StringUtils.isNoneEmpty(term)) {
			try {
				builder.queryParam("keyword", URLEncoder.encode(term, "UTF-8"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
				entity, String.class);

		JSONObject obj = new JSONObject(responseEntity.getBody());
		System.out.println(obj);
		if (obj.isNull("_embedded")) {
			return new ArrayList<>();
		}
		JSONObject embedded = obj.getJSONObject("_embedded");
		JSONArray events = embedded.getJSONArray("events");
		List<Item> items = getItemList(events);

		for (Item item : items) {
                             // we will update database using hibernate later
			try {
				dao.saveItem(item);
			} catch (Exception e) {
			}
		}
		return items;
	}

	public Set<Item> getUserFavorites(String userId) {
		return dao.getFavoriteItems(userId);
	}

	public void setUserFavorites(HistoryItem historyItem) {
		dao.setFavoriteItems(historyItem.getUser_id(), historyItem.getFavorite());
	}

	public void deleteUserFavorites(HistoryItem historyItem) {
		dao.unsetFavoriteItems(historyItem.getUser_id(), historyItem.getFavorite());
	}

	private JSONObject getVenue(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				if (venues.length() > 0) {
					return venues.getJSONObject(0);
				}
			}
		}
		return null;
	}
	
	public List<Item> getUserRecommendation(String userId, double lat, double lon) {
		return recommendation.recommendItems(userId, lat, lon);
	}


	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return null;
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			Set<String> categories = new HashSet<>();
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						categories.add(name);
					}
				}
			}
			return categories;
		}
		return null;
	}

	/**
	 * Helper methods
	 */
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);

			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}

			JSONObject venue = getVenue(event);
			if (venue != null) {
				StringBuilder sb = new StringBuilder();
				if (!venue.isNull("address")) {
					JSONObject address = venue.getJSONObject("address");
					if (!address.isNull("line1")) {
						sb.append(address.getString("line1"));
					}
					if (!address.isNull("line2")) {
						sb.append(address.getString("line2"));
					}
					if (!address.isNull("line3")) {
						sb.append(address.getString("line3"));
					}
					sb.append(",");
				}
				if (!venue.isNull("city")) {
					JSONObject city = venue.getJSONObject("city");
					if (!city.isNull("name")) {
						sb.append(city.getString("name"));
					}
				}
				builder.setAddress(sb.toString());
			}

			builder.setImageUrl(getImageUrl(event));
			builder.setCategories(getCategories(event));

			Item item = builder.build();
			itemList.add(item);
		}
		return itemList;
	}


}
