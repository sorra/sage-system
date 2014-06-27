package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.Comparators;
import sage.domain.repository.FavRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Fav;

@Service
@Transactional
public class FavService {
  @Autowired
  private FavRepository favRepo;
  @Autowired
  private UserRepository userRepo;
  
  @Transactional(readOnly=true)
  public Collection<Fav> favs(long userId) {
    List<Fav> favs = new ArrayList<>(favs(userId));
    Collections.sort(favs, Comparators.favOnId);
    return favs;
  }
  
  public void addFav(long userId, String link) {
    Fav fav = new Fav(link, userRepo.load(userId), new Date());
    favRepo.save(fav);
  }
  
  public boolean deleteFav(long userId, long favId) {
    Fav fav = favRepo.get(favId);
    if (fav == null) {
      return false;
    }
    
    if (fav.getOwner().getId().equals(userId)) {
      favRepo.delete(fav);
      return true;
    } else {
      return false;
    }
  }
}
