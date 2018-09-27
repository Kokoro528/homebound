package homebound.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebound.service.TicketMasterService;
import java.util.*;
import java.util.Map.Entry;

import homebound.dao.MysqlDao;
import homebound.entity.*;


@Service
public class GeoRecommendation {
	
	@Autowired
	private MysqlDao dao;
	
	@Autowired
	private TicketMasterService service;
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();

		// step 1 Get all favorite items
		Set<String> favoriteItems = dao.getFavoriteItemIds(userId);
		
		// step 2 Get all categories of favorite items, sort by count
		Map<String, Integer> allCategories = new HashMap<>(); // step 2
		for (String item : favoriteItems) {
			Set<String> categories = dao.getCategories(item); // db queries
			for (String category : categories) {
				if (allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category) + 1);
				} else {
					allCategories.put(category, 1);
				}
			}
		}

		List<Entry<String, Integer>> categoryList = new ArrayList<Entry<String, Integer>>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});

		// Step 3, do search based on category, filter out favorite events, sort by distance
		Set<Item> visitedItems = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = service.search(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			for (Item item : items) {
				if (!favoriteItems.contains(item.getItemId()) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			Collections.sort(filteredItems, new Comparator<Item>() {
				@Override
				public int compare(Item item1, Item item2) {
					// return the increasing order of distance.
					return Double.compare(item1.getDistance(), item2.getDistance());
				}
			});
			visitedItems.addAll(items);
			recommendedItems.addAll(filteredItems);
		}
		return recommendedItems;
  }
}

