//package net.projects.springboot.repository;
//
//import net.projects.springboot.entity.Post;
//import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
//import org.springframework.data.domain.Page;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface PostRepository extends JpaRepository<Post, Long> {
//    Optional<Post> findByUrl(String url);
//
//    @Query("SELECT p from Post p WHERE " +
//            " p.title LIKE CONCAT('%', :query, '%') OR " +
//            " p.shortDescription LIKE CONCAT('%', :query, '%')")
//    List<Post> searchPosts(String query);
//
//    @Query(value = "SELECT * from posts p where p.created_by =:userId")
//   List<Post> findPostsByUser(Long userId);
//
//    @Query(value = "SELECT * FROM posts p WHERE p.created_by = :userId",
//            nativeQuery = true)
////    Page<Post> findPostsByUser(Long userId, SpringDataWebProperties.Pageable pageable);
//    Page<Post> findPostsByUser(@Param("userId") Long userId, Pageable pageable);
//}

package net.projects.springboot.repository;

import net.projects.springboot.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUrl(String url);

    @Query("SELECT p FROM Post p WHERE " +
            "p.title LIKE CONCAT('%', :query, '%') OR " +
            "p.shortDescription LIKE CONCAT('%', :query, '%')")
    List<Post> searchPosts(@Param("query") String query);

    @Query("SELECT p FROM Post p WHERE " +
            "p.title LIKE CONCAT('%', :query, '%') OR " +
            "p.shortDescription LIKE CONCAT('%', :query, '%')")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.created_by = :userId",
            nativeQuery = true)
    List<Post> findPostsByUser(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.createdBy.id = :userId")
    Page<Post> findPostsByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );
    // Search only posts created by a specific user, with pagination
    @Query("SELECT p FROM Post p WHERE p.createdBy.id = :userId AND (" +
            "p.title LIKE CONCAT('%', :query, '%') OR " +
            "p.shortDescription LIKE CONCAT('%', :query, '%'))")
    Page<Post> searchPostsByUser(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable
    );
}
