package fet.datn.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralList<T> implements Serializable {
    private ResponseStatus status;
    private List<T> data;
}
