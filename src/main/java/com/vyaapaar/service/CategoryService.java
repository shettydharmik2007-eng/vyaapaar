package com.vyaapaar.service;

import com.vyaapaar.dao.CategoryDao;
import com.vyaapaar.model.Category;

import java.util.List;

/**
 * Service managing Category CRUD operations and validations.
 */
public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService() {
        this.categoryDao = new CategoryDao();
    }

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * Adds a new category after validating the name.
     *
     * @param categoryName Name of the category
     * @param description Brief description
     * @return Created Category object
     * @throws IllegalArgumentException if name is invalid
     */
    public Category addCategory(String categoryName, String description) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        Category category = new Category(categoryName.trim(), description != null ? description.trim() : "");
        boolean success = categoryDao.addCategory(category);
        if (!success) {
            throw new RuntimeException("Failed to add category. Category name may already exist.");
        }
        return category;
    }

    /**
     * Retrieves all categories.
     */
    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    /**
     * Retrieves a category by its ID.
     */
    public Category getCategoryById(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " does not exist.");
        }
        return category;
    }

    /**
     * Updates an existing category.
     */
    public boolean updateCategory(int categoryId, String categoryName, String description) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        getCategoryById(categoryId); // Verify exists
        Category updated = new Category(categoryId, categoryName.trim(), description != null ? description.trim() : "");
        return categoryDao.updateCategory(updated);
    }

    /**
     * Deletes a category by its ID.
     */
    public boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }
        getCategoryById(categoryId); // Verify exists
        return categoryDao.deleteCategory(categoryId);
    }
}
