package com.unicartagena.muebles_ana_api.base.utils.converters;

import com.unicartagena.muebles_ana_api.application.entities.Rol;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RolConverter extends EnumLowerCaseConverter<Rol> {

    public RolConverter() {
        super(Rol.class);
    }
}
