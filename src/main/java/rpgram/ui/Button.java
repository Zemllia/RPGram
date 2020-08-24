package rpgram.ui;

import com.crown.i18n.I18n;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import rpgram.creatures.Human;

public enum Button {
    up("move-up", "^"),
    down("move-down", "v"),
    left("move-left", "<"),
    right("move-right", ">"),
    inventory("inventory", "rpgram.ui.inventory"),
    map("map", "map"),
    actions("actions", "rpgram.ui.actions"),
    legend("legend", "?"),
    talk("talk", "rpgram.ui.talk"),
    talkStop("stop-talk", "rpgram.ui.talk.stop"),
    upgradeFov("upgrade-fov", "rpgram.ui.adjust.fov"),
    upgradeHp("upgrade-health", "rpgram.ui.adjust.hp"),
    sleep("sleep", "rpgram.ui.sleep"),
    enter("enter", "rpgram.ui.enter"),
    exit("exit", "rpgram.ui.exit"),
    back("back", "rpgram.ui.back"),
    cancel("cancel", "rpgram.ui.cancel");

    private final String callback;
    private final String label;

    Button(String callback, String label) {
        this.callback = callback;
        this.label = label;
    }

    public String getCallback() {
        return callback;
    }

    public InlineKeyboardButton get() {
        return new InlineKeyboardButton()
            .setText(I18n.of(label).getLocalized())
            .setCallbackData(callback);
    }

    public InlineKeyboardButton get(Human player) {
        return new InlineKeyboardButton()
            .setText(I18n.of(label).getLocalized(player.lang))
            .setCallbackData(callback);
    }
}
