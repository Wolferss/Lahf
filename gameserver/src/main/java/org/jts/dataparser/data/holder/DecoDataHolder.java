package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.holder.decodata.Deco;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Camelion
 * @date : 26.08.12 21:44
 */
public class DecoDataHolder extends AbstractHolder {
    private static final Pattern funcPattern = Pattern.compile("func=(none|hp_regen\\(([\\d\\.]+)\\)|mp_regen\\(([\\d\\.]+)\\)|exp_restore\\(([\\d\\.]+)\\))");
    private static DecoDataHolder ourInstance = new DecoDataHolder();
    @Element(start = "deco_begin", end = "deco_end", objectFactory = DecoObjectFactory.class)
    private List<Deco> decos;

    private DecoDataHolder() {
    }

    public static DecoDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return decos.size();
    }

    public List<Deco> getDecos() {
        return decos;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    public static class DecoObjectFactory implements IObjectFactory<Deco> {
        @Override
        public Deco createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            Deco deco = new Deco();
            Matcher matcher = funcPattern.matcher(data);
            matcher.find();
            String value = matcher.group(1);
            if (value.contains("none")) {
                deco.func = Deco.DecoFunc.none;
            } else {
                String param = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
                value = value.substring(0, value.indexOf("("));
                deco.func = Deco.DecoFunc.valueOf(value);
                deco.func_param = Double.valueOf(param);
            }
            return deco;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignored
        }
    }
}