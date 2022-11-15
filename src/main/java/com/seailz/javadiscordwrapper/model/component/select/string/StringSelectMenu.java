package com.seailz.javadiscordwrapper.model.component.select.string;

import com.seailz.javadiscordwrapper.model.component.ActionComponent;
import com.seailz.javadiscordwrapper.model.component.ComponentType;
import com.seailz.javadiscordwrapper.model.component.select.SelectMenu;
import com.seailz.javadiscordwrapper.model.component.select.SelectOption;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a string select menu
 *
 * @author Seailz
 * @since  1.0
 * @see    com.seailz.javadiscordwrapper.model.component.select.SelectMenu
 */
public class StringSelectMenu implements SelectMenu {

    private String customId;
    private String placeholder;
    private int minValues;
    private int maxValues;
    private SelectOption[] options;
    private boolean disabled;

    /**
     * Creates a new string select menu
     * @param customId The custom id of the select menu
     * @param options The options of the select menu
     */
    public StringSelectMenu(String customId, List<SelectOption> options) {
        this.customId = customId;
        this.options = options.toArray(new SelectOption[0]);
    }

    /**
     * Creates a new string select menu
     * @param customId The custom id of the select menu
     * @param placeholder The placeholder of the select menu
     * @param minValues The minimum amount of values that can be selected
     * @param maxValues The maximum amount of values that can be selected
     * @param options The options of the select menu
     */
    public StringSelectMenu(String customId, String placeholder, int minValues, int maxValues, List<SelectOption> options, boolean disabled) {
        this.customId = customId;
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.options = options.toArray(new SelectOption[0]);
        this.disabled = disabled;
    }

    /**
     * Creates a new string select menu
     * @param customId The custom id of the select menu
     * @param selectOptions The options of the select menu
     */
    public StringSelectMenu(String customId, SelectOption... selectOptions) {
        this(customId, new ArrayList<>(List.of(selectOptions)));
    }

    @Override
    public String customId() {
        return customId;
    }

    @Override
    public String placeholder() {
        return placeholder;
    }

    @Override
    public int minValues() {
        return minValues;
    }

    @Override
    public int maxValues() {
        return maxValues;
    }

    public StringSelectMenu setCustomId(String customId) {
        this.customId = customId;
        return this;
    }

    public StringSelectMenu setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public StringSelectMenu setMinValues(int minValues) {
        this.minValues = minValues;
        return this;
    }

    public StringSelectMenu setMaxValues(int maxValues) {
        this.maxValues = maxValues;
        return this;
    }

    public StringSelectMenu setOptions(SelectOption[] options) {
        this.options = options;
        return this;
    }

    public SelectOption[] options() {
        return options;
    }

    public StringSelectMenu addOption(SelectOption option) {
        SelectOption[] newOptions = new SelectOption[options.length + 1];
        System.arraycopy(options, 0, newOptions, 0, options.length);
        newOptions[newOptions.length - 1] = option;
        options = newOptions;
        return this;
    }



    @Override
    public ActionComponent setDisabled(boolean disabled) {
        return new StringSelectMenu(
                customId, placeholder, minValues, maxValues, List.of(options), disabled
        );
    }

    @Override
    public ComponentType type() {
        return ComponentType.STRING_SELECT;
    }

    @Override
    public boolean isSelect() {
        return true;
    }

    @Override
    public JSONObject compile() {
        JSONArray options = new JSONArray();
        for (SelectOption option : this.options)
            options.put(option.compile());

        if (maxValues > options.length())
            throw new IllegalArgumentException("Max values cannot be greater than the amount of options");

        if (minValues > maxValues)
            throw new IllegalArgumentException("Min values cannot be greater than max values");

        if (options.length() == 0)
            throw new IllegalArgumentException("Select menu must have at least one option");

        if (options.length() > 25)
            throw new IllegalArgumentException("Select menu cannot have more than 25 options");

        JSONObject obj = new JSONObject();
        obj.put("type", type().getCode());
        obj.put("custom_id", customId);
        if (placeholder != null) obj.put("placeholder", placeholder);
        if (minValues != 0) obj.put("min_values", minValues);
        if (maxValues != 0) obj.put("max_values", maxValues);
        if (disabled) obj.put("disabled", true);
        obj.put("options", options);
        return obj;
    }

    public static StringSelectMenu decompile(JSONObject json) {
        String customId = json.has("custom_id") ? json.getString("custom_id") : null;
        String placeholder = json.has("placeholder") ? json.getString("placeholder") : null;
        int minValues = json.has("min_values") ? json.getInt("min_values") : 0;
        int maxValues = json.has("max_values") ? json.getInt("max_values") : 25;
        List<SelectOption> options = new ArrayList<>();

        if (json.has("options")) {
            JSONArray optionsJson = json.getJSONArray("options");
            for (int i = 0; i < optionsJson.length(); i++)
                options.add(SelectOption.decompile(optionsJson.getJSONObject(i)));
        }

        return new StringSelectMenu(customId, placeholder, minValues, maxValues, options, json.has("disabled") && json.getBoolean("disabled"));
    }

}
