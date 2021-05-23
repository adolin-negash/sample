package adolin.sample.infra.updatable;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Общая информация об обновляемом свойстве.
 *
 * @author Adolin Negash 23.05.2021
 */
class PropertyInfo {

    /**
     * Значение свойства.
     */
    @Getter
    private String value;

    /**
     * Список элементов бинов (поля и сеттеры), которые привязаны к этому свойству.
     */
    private final List<BeanMemberInfo> members = new ArrayList<>();

    /**
     * Создает объект с информацией об обновляемых свойствах.
     *
     * @param value начальное значение свойства.
     */
    PropertyInfo(String value) {
        this.value = value;
    }

    /**
     * Добавляет элемент бина.
     *
     * @param memberInfo элемент бина.
     */
    void addMember(BeanMemberInfo memberInfo) {
        members.add(memberInfo);
    }

    /**
     * Проставляет значение свойства.
     *
     * @param value значение свойства.
     */
    void setValue(String value) {
        this.value = value;

        for (BeanMemberInfo member : members) {
            member.setValue(value);
        }
    }

    /**
     * Возвращает true, если список элементов бинов пуст. Иначе - false.
     */
    boolean isEmpty() {
        return members.isEmpty();
    }
}
