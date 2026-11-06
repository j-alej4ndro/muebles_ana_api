package com.unicartagena.muebles_ana_api.base.utils.converters;

import jakarta.persistence.AttributeConverter;

import java.util.Locale;

public abstract class EnumLowerCaseConverter<E extends Enum<E>> implements AttributeConverter<E, String> {

    private final Class<E> tipo;

    protected EnumLowerCaseConverter(Class<E> tipo) {
        this.tipo = tipo;
    }

    @Override
    public String convertToDatabaseColumn(E atributo) {
        return atributo == null ? null : atributo.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public E convertToEntityAttribute(String columna) {
        return columna == null ? null : Enum.valueOf(tipo, columna.toUpperCase(Locale.ROOT));
    }
}
