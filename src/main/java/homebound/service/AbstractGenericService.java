package homebound.service;
import java.util.List;
public abstract class AbstractGenericService<T> implements GenericService<T> {
	
	@Override
	public List<T> search(double latitude, double longitude, String term) {
		throw new UnsupportedOperationException("search()");
	}
}
