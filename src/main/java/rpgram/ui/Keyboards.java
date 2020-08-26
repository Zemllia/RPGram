package rpgram.ui;

import com.crown.i18n.I18n;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import rpgram.creatures.Human;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Keyboards {
    public static InlineKeyboardMarkup arrows(Human player) {
        return new InlineKeyboardMarkup().setKeyboard(list(
            list(Button.up.get()),
            list(Button.left.get(), Button.right.get()),
            list(Button.down.get()),
            list(
                Button.inventory.get(player),
                Button.map.get(player),
                Button.actions.get(player),
                Button.legend.get()
            )
        ));
    }

    public static InlineKeyboardMarkup actionsMenu(Human player) {
        return new InlineKeyboardMarkup()
            .setKeyboard(list(
                // list(Button.talk.get(player)), TODO: talk button handler
                list(Button.sleep.get(player)),
                list(
                    Button.upgradeFov.get(player),
                    Button.upgradeHp.get(player)
                ),
                list(Button.back.get(player))
            ));
    }

    public static InlineKeyboardMarkup stopTalking(Human player) {
        return new InlineKeyboardMarkup().setKeyboard(list(
            list(Button.talkStop.get(player))
        ));
    }

    public static InlineKeyboardMarkup listOfItems(Human player) {
        var put = I18n.of("rpgram.ui.put").getLocalized(player.lang);
        return new InlineKeyboardMarkup().setKeyboard(
            list(
                player.getInventory().stream().map(
                    ii -> {
                        var itemName = ii.getName().getLocalized(player.lang);
                        return new InlineKeyboardButton()
                            .setText(put + " " + itemName)
                            .setCallbackData("put-" + ii.getKeyName());
                    }).collect(Collectors.toList()),
                list(Button.cancel.get(player))
            )
        );
    }

    @SafeVarargs
    static <T> List<T> list(T... items) {
        return Arrays.asList(items);
    }
}
