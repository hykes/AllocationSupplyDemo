package com.github.hykes.extend;

import java.math.BigDecimal;

/**
 * @author hehaiyangwork@gmail.com
 * @date 2019-12-28 19:38:00
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T>  {

    BigDecimal applyAsBigDecimal(T value);

}
