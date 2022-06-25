package swapper.intentions.tropical.module;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.settings.Setting;

public abstract class Module {
    private String name, description, displayName, suffix = "";
    private int key;
    private boolean toggled, hidden;
    private Category category;
    private ArrayList<Setting> settings = new ArrayList<>();
    public static Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, String description, int key, Category c){
        this.name = name;
        this.displayName = name;
        this.description = description;
        this.key = key;
        this.category = c;
    }

    protected void onEnable() {}

    public void onRenderAlways() {} //g8lol dont kill me... i just wanna add always updating hidden settings :(

    protected void onDisable() {}

	public ArrayList<Setting> getSettings() {
		return settings;
	}
	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}

    public void toggle() {
        toggled = !toggled;
        if(toggled) {
            onEnable();
            Tropical.instance.eventBus.register(this);
        }else {
            Tropical.instance.eventBus.unregister(this);
            onDisable();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFormattedName() {
        return displayName + "\2477" + (suffix.length() > 1 ? (" " + suffix) : "");
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public enum Category {

        COMBAT("Combat"),
        EXPLOIT("Exploit"),
        MOVEMENT("Movement"),
        VISUALS("Visuals"),
        PLAYER("Player"),
        WORLD("World");

        public String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}