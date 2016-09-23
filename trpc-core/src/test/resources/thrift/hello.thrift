namespace java com.github.sofn.trpc.demo

service Hello {
  string hi(1:string name);
}