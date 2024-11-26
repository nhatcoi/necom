'use restrict';

(function ($) {
    $(document).ready(function() {
        const PREFIX = '/api/v1';
        // When clicking on the blog item link
        function getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(param);
        }

        // Get the blog ID from the URL
        const blogId = getQueryParam('id');

        if (blogId) {
            $.ajax({
                url: `${PREFIX}/blog-details/${blogId}`,// API endpoint to fetch blog details
                type: 'GET',
                success: function (data) {
                    // Assuming `data` is a JSON object with all necessary fields
                    $('#blog-title').text(data.title);
                    $('#blog-author').text(`By ${data.userId}`);
                    $('#blog-date').txxxxext(data.createAt);
                    // $('#blog-comments').text(`${data.comments} Comments`);
                    $('#blog-image').attr('src', data.thumbnail);
                    $('#blog-content').text(data.content);
                    // $('#author-image').attr('src', data.authorImage);
                    // $('#author-name').text(data.authorName);
                    // $('#author-role').text(data.authorRole);
                    $('#blog-categories').text(data.categories.join(', '));
                    $('#blog-tags').text(data.tags.join(', '));
                },
                error: function (xhr, status, error) {
                    console.error('Failed to fetch blog details:', error);
                    $('#blog-content').text('Error loading blog details.');
                }
            });
        } else {
            $('#blog-content').text('Blog ID not found.');
        }
    });



} (jQuery));