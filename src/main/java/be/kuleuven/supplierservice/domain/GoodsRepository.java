package be.kuleuven.supplierservice.domain;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class GoodsRepository {
    private static final Map<String, Good> goods = new HashMap<>();

    @PostConstruct
    public void initData(){
        Good a = new Good("sRZJMHGkZDUMReAXigb9","62dzzc-de76-4921-a3e3-439db69c462a","AJ4 white","https://firebasestorage.googleapis.com/v0/b/dapp2024-service.appspot.com/o/DALL%C2%B7E%202024-05-18%2012.51.06%20-%20A%20detailed%20image%20of%20an%20Air%20Jordan%204%20sneaker.%20The%20shoe%20should%20have%20a%20stylish%2C%20modern%20design%2C%20featuring%20the%20iconic%20netted%20panels%20on%20the%20sides%2C%20a%20visible.webp?alt=media&token=419d74ff-23ff-4fdb-9525-47cb55b47fd4",200,"male");
        this.generateInventory(a);
        goods.put(a.getId(),a);

        Good b = new Good("sRZJMHGkZDUMReAXigb9","6268ddc-de76-4921-a3e3-439db69c462a","AJ1 Red Black","https://firebasestorage.googleapis.com/v0/b/dapp2024-service.appspot.com/o/DALL%C2%B7E%202024-05-18%2012.59.49%20-%20A%20detailed%20image%20of%20an%20Air%20Jordan%201%20sneaker%20in%20the%20classic%20black%20and%20red%20color%20scheme%2C%20also%20known%20as%20'Bred'.%20The%20shoe%20should%20have%20a%20stylish%2C%20modern%20de.webp?alt=media&token=c13d3035-05ee-425c-baf6-85d1282111cf",150,"female");
        this.generateInventory(b);
        goods.put(b.getId(),b);

        Good c = new Good("sRZJMHGkZDUMReAXigb9","6268203c-de76-4921-a3e3-439ddasfs62a","luka 2 white","https://firebasestorage.googleapis.com/v0/b/dapp2024-service.appspot.com/o/DALL%C2%B7E%202024-05-18%2013.00.44%20-%20A%20detailed%20image%20of%20an%20Air%20Jordan%20Luka%202%20sneaker%20in%20a%20clean%2C%20white%20color%20scheme.%20The%20shoe%20should%20feature%20a%20modern%20and%20sleek%20design%2C%20with%20elements%20that.webp?alt=media&token=3bcc3df9-dbe5-4e19-80c4-7223135d44ed",200,"male");
        this.generateInventory(c);
        goods.put(c.getId(),c);

    }

    private void generateInventory(Good good) {
        Random random = new Random();
        for (int size = 35; size <= 46; size++) {
            String id = UUID.randomUUID().toString(); // Generate a unique UUID
            int stock = random.nextInt(100); // Random stock between 0 and 99
            int reserved = random.nextInt(50); // Random reserved between 0 and 49
            int sold = random.nextInt(50); // Random sold between 0 and 49
            Inventory inventory = new Inventory(id, size, stock, reserved, sold);
            good.addInventory(inventory);
        }
    }

    public Optional<Good> findGood(String id) {
        Assert.notNull(id, "The good id must not be null");
        Good good = goods.get(id);
        return Optional.ofNullable(good);
    }

    public void addGood(Good good){
        Assert.notNull(good.getId(), "Meal ID must not be null");
        goods.put(good.getId(), good);
    }

    public Good updatedGood(String id, Good updatedGood) {
        updatedGood.setId(id);
        goods.put(id, updatedGood);
        return updatedGood;
    }

    // Method to delete a meal
    public void deleteGood(String id) {
        goods.remove(id);
    }

    public Collection<Good> getAllGood() {
        return goods.values();
    }
}
