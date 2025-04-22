package com.example.ecommerceweb.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blogs")
@Entity
@Builder
public class Blog extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "views")
    private Integer views;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blog_category_id")
    private BlogCategory blogCategory;
}
