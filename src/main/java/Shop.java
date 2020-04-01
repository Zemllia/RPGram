import core.GameObject;
import core.Position;
import items.ShopItem;

import java.util.ArrayList;

public class Shop extends GameObject {

    ArrayList<ShopItem> shopInventory = new ArrayList<>();

    int shopId;
    char mapIcon;

    int shopMoney;

    public Shop(String shopName, Position shopPosition, int shopId, char mapIcon, ShopItem[] shopItems, int shopMoney) {
        super(shopName, shopPosition, 0, 's');
        this.shopId = shopId;
        this.mapIcon = mapIcon;
        this.shopMoney = shopMoney;
        for (ShopItem item : shopItems) {
            shopInventory.add(item);
        }
    }

    public ShopItem getItem(int itemId) {
        if (itemId < shopInventory.size() - 1) {
            return shopInventory.get(itemId);
        }
        return null;
    }

    public ArrayList<ShopItem> getItems() {
        return shopInventory;
    }

    public ShopItem buyItem(int itemId, int count) {
        if (itemId < shopInventory.size() - 1) {
            ShopItem itemToWork = shopInventory.get(itemId);
            if (count <= itemToWork.getCount()) {
                itemToWork.setCount(itemToWork.getCount() - count);
                ShopItem itemToReturn = itemToWork;
                if (itemToWork.getCount() == 0) {
                    shopInventory.remove(itemToWork);
                }
                shopMoney += itemToReturn.getCost() * count;
                return itemToReturn;
            }
        }
        return null;
    }
    /*public void sellItem(InventoryItem){

    }*/
}
