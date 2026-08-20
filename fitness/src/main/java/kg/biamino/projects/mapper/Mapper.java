package kg.biamino.projects.mapper;

public interface Mapper<E,D> {
    E toEntity(D t);
    D toDto(E e);
}
