package homebound.service;

import java.util.*;

public interface GenericService<T> {
	
	List<T> search(double latitude, double longitude, String term);
	
}
