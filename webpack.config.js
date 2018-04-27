module.exports = {
  context: __dirname,
  devtool: "source-map",
  entry: __dirname + "/src/main/webapp/lib/burner/index.js",
  output: {
    path: __dirname + "/src/main/webapp/lib/burner",
    filename: "dist/bundle.min.js"
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['babel-preset-env']
          }
        }
      }
    ]
  },
};