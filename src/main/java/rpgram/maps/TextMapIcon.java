package rpgram.maps;

import com.crown.i18n.I18n;
import com.crown.i18n.ITemplate;
import com.crown.maps.MapIcon;
import rpgram.ui.IconType;

public class TextMapIcon extends MapIcon<String> {
    private final String text;

    public TextMapIcon(String keyName, String representation) {
        super(keyName);
        this.text = representation;
    }

    @Override
    public ITemplate getName() {
        return I18n.empty;
    }

    @Override
    public ITemplate getDescription() {
        return I18n.empty;
    }

    public String get() {
        return text;
    }

    @Override
    public void stepAnimation() {

    }

    @Override
    public String toString() {
        return text;
    }
}
