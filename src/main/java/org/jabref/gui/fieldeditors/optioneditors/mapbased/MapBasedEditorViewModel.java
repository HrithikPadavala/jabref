package org.jabref.gui.fieldeditors.optioneditors.mapbased;

import java.util.Collection;

import javax.swing.undo.UndoManager;

import javafx.util.StringConverter;

import org.jabref.gui.autocompleter.SuggestionProvider;
import org.jabref.gui.fieldeditors.optioneditors.OptionEditorViewModel;
import org.jabref.logic.integrity.FieldCheckers;
import org.jabref.model.entry.field.Field;

import com.google.common.collect.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View model for a field editor that shows various options backed by a map.
 */
public abstract class MapBasedEditorViewModel<T> extends OptionEditorViewModel<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapBasedEditorViewModel.class);

    public MapBasedEditorViewModel(Field field, SuggestionProvider<?> suggestionProvider, FieldCheckers fieldCheckers, UndoManager undoManager) {
        super(field, suggestionProvider, fieldCheckers, undoManager);
    }

    protected abstract BiMap<String, T> getItemMap();

    @Override
    public StringConverter<T> getStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                if (object == null) {
                    return null;
                } else {
                    // if the object is not found we simply return itself as string
                    return getItemMap().inverse().getOrDefault(object, object.toString());
                }
            }

            @Override
            public T fromString(String string) {
                if (string == null) {
                    return null;
                } else {
                    return getItemMap().getOrDefault(string, getValueFromString(string));
                }
            }
        };
    }

    /**
     * Converts a String value to the Type T. If the type cannot be directly cast to T, this method must be overridden in a subclass
     *
     * @param string The input value to convert
     * @return The value or null if the value could not be cast
     */
    @SuppressWarnings("unchecked")
    protected T getValueFromString(String string) {
        try {
            return (T) string;
        } catch (ClassCastException ex) {
            LOGGER.error("Could not cast string to type %1$s. Try overriding the method in a subclass and provide a conversion from string to the concrete type %1$s".formatted(string.getClass()), ex);
        }
        return null;
    }

    @Override
    public Collection<T> getItems() {
        return getItemMap().values();
    }
}
