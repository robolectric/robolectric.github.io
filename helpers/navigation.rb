module Navigation
    def render_nav(groups)
        "".tap do |html|
            groups.each do |group|
                html << render_nav_list(group, sitemap.where(:group => group).order_by(:order).all)
            end
        end
    end

    private

    def render_nav_list(title, pages)
        "".tap do |html|
            html << "<ul class='nav nav-stacked'>"
            html << "<li class='nav-header'>#{title}</li>"
            pages.each do |page|
                html << render_nav_item(page) if page.data['title'].present?
            end
            html << "</ul>"
        end
    end

    def render_nav_item(page)
        "<li class='nav-item'><a href='#{page.url}'>#{page.data['title']}</a></li>"
    end
end