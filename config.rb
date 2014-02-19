set :js_dir, 'javascripts'
set :css_dir, 'stylesheets'
set :images_dir, 'images'

set :markdown_engine, :redcarpet
set :markdown, fenced_code_blocks: true, autolink: true, smartypants: true

activate :syntax
activate :directory_indexes

page "/javadoc/*", :directory_index => false

activate :deploy do |deploy|
    deploy.method = :git
    deploy.remote = 'origin'
    deploy.branch = 'master'
end

configure :build do
    activate :asset_hash
    activate :minify_css
    activate :minify_javascript
    activate :relative_assets
    activate :google_analytics do |ga|
        ga.tracking_id = 'UA-19269905-1'
    end
end

configure :development do
    activate :google_analytics do |ga|
        ga.tracking_id = false
    end
end
