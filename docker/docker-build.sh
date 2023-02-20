current_dir=$(pwd)
# -----------------------------------------------------------------------------
# build recommendation service docker image
# -----------------------------------------------------------------------------
cd .. || exit
project_version=$(cat pom.xml | grep "<version>.*</version>" | head -1 |awk -F'[><]' '{print $3}')
component_name=$(cat pom.xml | grep "<artifactId>.*</artifactId>" | head -1 |awk -F'[><]' '{print $3}')

echo "Component name:     $component_name"
echo "Project version:    $project_version"

cd "$current_dir" || exit

docker build -f build.dockerfile -t "$component_name":"$project_version" ..