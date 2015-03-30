set :js_dir, 'javascripts'
set :css_dir, 'stylesheets'
set :images_dir, 'images'

set :markdown, tables: true, autolink: true, gh_blockquote: true, fenced_code_blocks: true
set :markdown_engine, :redcarpet

activate :syntax
activate :directory_indexes

page "/javadoc/*", directory_index: false
page "/*", layout: 'default'

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
        ga.tracking_id = 'UA-58882116-1'
    end
end

configure :development do
    activate :google_analytics do |ga|
        ga.tracking_id = false
    end
end
