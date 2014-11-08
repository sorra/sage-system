package sage.domain.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.Comparators;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.FavRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Fav;
import sage.transfer.FavInfo;

@Service
@Transactional
public class FavService {
  @Autowired
  private FavRepository favRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TweetReadService tweetRead;
  
  @Transactional(readOnly=true)
  public Collection<FavInfo> favs(long userId) {
    List<Fav> favs = new ArrayList<>(favRepo.favs(userId));
    Collections.sort(favs, Comparators.favOnId);

    return FavInfo.listOf(favs, tweetRead::getTweetCard);
  }
  
  public void addFav(long userId, String link) {
    Fav fav = new Fav(link, userRepo.load(userId), new Date());
    favRepo.save(fav);
  }
  
  public void deleteFav(long userId, long favId) {
    Fav fav = favRepo.get(favId);
    if (!IdCommons.equal(fav.getOwner().getId(), userId)) {
      throw new DomainRuntimeException("User[%d] is not the owner of Fav[%d]", userId, favId);
    }
    favRepo.delete(fav);
  }
}
