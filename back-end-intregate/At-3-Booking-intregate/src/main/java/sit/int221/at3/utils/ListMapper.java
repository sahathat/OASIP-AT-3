package sit.int221.at3.utils;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListMapper {
    private static final ListMapper listMapper = new ListMapper();
    private ListMapper() {}
    public <S,T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass)).collect(Collectors.toList());
    }
    public <S,T> Set<T> mapSet(Set<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass)).collect(Collectors.toSet());
    }
    public static ListMapper getInstance(){
        return listMapper;
    }
}
