package org.jts.dataparser.data.holder.variationdata.support;

import java.util.ArrayList;
import java.util.List;

public class VariationInfo {
    private final int _id;
    private final List<VariationCategory> _categories = new ArrayList<VariationCategory>();

    public VariationInfo(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void addCategory(VariationCategory category) {
        _categories.add(category);
    }

    public VariationCategory[] getCategories() {
        return _categories.toArray(new VariationCategory[_categories.size()]);
    }
}
